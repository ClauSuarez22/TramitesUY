package uy.edu.ucu.android.parser.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uy.edu.ucu.android.parser.model.Category;
import uy.edu.ucu.android.parser.model.Dependence;
import uy.edu.ucu.android.parser.model.Link;
import uy.edu.ucu.android.parser.model.Location;
import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.parser.model.TakeIntoAccount;
import uy.edu.ucu.android.parser.model.WhenAndWhere;

/**
 * IMPORTANT! YOU SHOULD NOT CHANGE THIS CLASS
 */
public class ProceedingXMLParser {

    public static List<Proceeding> readProceedings(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            List proceedings = new ArrayList();

            parser.require(XmlPullParser.START_TAG, null, Constants.TAG_TRAMITE_TODOS);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals(Constants.TAG_TRAMITE)) {
                    Proceeding proceeding = readProceeding(parser);
                    if(proceeding.getWhenAndWhere() != null && proceeding.getWhenAndWhere().getLocations().size() > 0){
                        proceedings.add(proceeding);

                    }
                } else {
                    SimpleXMLParser.skip(parser);
                }
            }

            return proceedings;

        } finally {
            in.close();
        }
    }

    private static Proceeding readProceeding(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, Constants.TAG_TRAMITE);

        Proceeding proceeding = new Proceeding();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case Constants.TAG_ID_TRAMITES_GUB_UY:
                    proceeding.setId(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_URL_TRAMITES_GUB_UY:
                    proceeding.setUrl(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_TITULO:
                    proceeding.setTitle(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_QUE_ES:
                    proceeding.setDescription(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_ACCESO_ONLINE:
                    proceeding.setOnlineAccess(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_REQUISITOS:
                    proceeding.setRequisites(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_COMO_SE_HACE:
                    proceeding.setProcess(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_MAIL_CONSULTA:
                    proceeding.setMail(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_ESTADO:
                    proceeding.setStatus(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_VINCULOS:
                    parser.require(XmlPullParser.START_TAG, null, Constants.TAG_VINCULOS);
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser.getName();
                        // Starts by looking for the entry tag
                        if (name.equals(Constants.TAG_VINCULO)) {
                            proceeding.getLinks().add(readLink(parser));
                        } else {
                            SimpleXMLParser.skip(parser);
                        }
                    }
                    break;
                case Constants.TAG_CATEGORIAS:
                    parser.require(XmlPullParser.START_TAG, null, Constants.TAG_CATEGORIAS);
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser.getName();
                        // Starts by looking for the entry tag
                        if (name.equals(Constants.TAG_CATEGORIA)) {
                            proceeding.getCategories().add(readCategory(parser));
                        } else {
                            SimpleXMLParser.skip(parser);
                        }
                    }
                    break;
                case Constants.TAG_DONDE_Y_CUANDO:
                    parser.require(XmlPullParser.START_TAG, null, Constants.TAG_DONDE_Y_CUANDO);
                    WhenAndWhere whenAndWhere = new WhenAndWhere();
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser.getName();
                        // Starts by looking for the entry tag
                        if (name.equals(Constants.TAG_UBICACION)) {
                            whenAndWhere.getLocations().add(readLocation(parser));
                        } else if (name.equals(Constants.TAG_OTROS_DATOS_UBICACION)) {
                            whenAndWhere.setOtherData(SimpleXMLParser.readTag(parser, name));
                        } else {
                            SimpleXMLParser.skip(parser);
                        }
                    }
                    proceeding.setWhenAndWhere(whenAndWhere);
                    break;
                case Constants.TAG_DEPENDE_DE:
                    parser.require(XmlPullParser.START_TAG, null, Constants.TAG_DEPENDE_DE);
                    Dependence dependence = new Dependence();
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser.getName();
                        // Starts by looking for the entry tag
                        switch (name) {
                            case Constants.TAG_ORGANISMO:
                                dependence.setOrganization(SimpleXMLParser.readTag(parser, name));
                                break;
                            case Constants.TAG_AREA:
                                dependence.setArea(SimpleXMLParser.readTag(parser, name));
                                break;
                            default:
                                SimpleXMLParser.skip(parser);
                                break;
                        }
                    }
                    proceeding.setDependence(dependence);
                    break;
                case Constants.TAG_TENER_EN_CUENTA:
                    parser.require(XmlPullParser.START_TAG, null, Constants.TAG_TENER_EN_CUENTA);
                    TakeIntoAccount takeIntoAccount = new TakeIntoAccount();
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser.getName();
                        // Starts by looking for the entry tag
                        if (name.equals(Constants.TAG_FORMA_DE_SOLICITARLO)) {
                            takeIntoAccount.setHowToApply(SimpleXMLParser.readTag(parser, name));
                        } else if (name.equals(Constants.TAG_COSTO)) {
                            takeIntoAccount.setCost(SimpleXMLParser.readTag(parser, name));
                        } else if (name.equals(Constants.TAG_OTROS_DATOS_DE_INTERES)) {
                            takeIntoAccount.setOtherData(SimpleXMLParser.readTag(parser, name));
                        } else {
                            SimpleXMLParser.skip(parser);
                        }
                    }
                    proceeding.setTakeIntoAccount(takeIntoAccount);
                    break;
                default:
                    SimpleXMLParser.skip(parser);
                    break;
            }
        }

        return proceeding;
    }

    private static Category readCategory(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, Constants.TAG_CATEGORIA);

        Category category = new Category();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case Constants.TAG_TEMA:
                    category.setCode(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_TEMA_NOMBRE:
                    category.setName(SimpleXMLParser.readTag(parser, name));
                    break;
                default:
                    SimpleXMLParser.skip(parser);
                    break;
            }
        }

        return category;
    }

    private static Link readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, Constants.TAG_VINCULO);

        Link link = new Link();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case Constants.TAG_NOMBRE_LINK:
                    link.setName(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_LINK:
                    link.setUrl(SimpleXMLParser.readTag(parser, name));
                    break;
                default:
                    SimpleXMLParser.skip(parser);
                    break;
            }
        }

        return link;
    }

    private static Location readLocation(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, Constants.TAG_UBICACION);

        Location location = new Location();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case Constants.TAG_URUGUAY_O_EXTERIOR:
                    location.setIsUruguay(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_DEPARTAMENTO:
                    location.setState(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_LOCALIDAD:
                    location.setCity(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_DIRECCION:
                    location.setAddress(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_HORARIO:
                    location.setTime(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_TELEFONO:
                    location.setPhone(SimpleXMLParser.readTag(parser, name));
                    break;
                case Constants.TAG_COMENTARIOS:
                    location.setComments(SimpleXMLParser.readTag(parser, name));
                    break;
                default:
                    SimpleXMLParser.skip(parser);
                    break;
            }
        }

        return location;
    }

}
