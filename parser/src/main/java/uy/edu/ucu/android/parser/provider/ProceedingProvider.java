package uy.edu.ucu.android.parser.provider;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.parser.util.ProceedingXMLParser;


/**
 * Singleton class that loads the tramites.xml file and executes the parsing process
 * It holds a reference to the proceedings list once parsed, that you can retrieve with the getProceedings static method
 * This is used by the Utils class in your project to load the proceedings file
 * IMPORTANT! YOU SHOULD NOT CHANGE THIS CLASS
 */
public class ProceedingProvider {

    private static final String TAG = ProceedingProvider.class.getSimpleName();

    private static ProceedingProvider instance = null;
    private static List<Proceeding> proceedings;

    protected ProceedingProvider() {}

    public static ProceedingProvider getInstance(Context context) {
        if(instance == null) {
            instance = new ProceedingProvider();
            try {
                Log.d(TAG, "Opening tramites.xml file ...");
                InputStream is = context.getAssets().open("tramites.xml");
                Log.d(TAG, "Parsing proceedings ...");
                proceedings = ProceedingXMLParser.readProceedings(is);
                Log.d(TAG, "Finished parsing proceedings. We have " + proceedings.size() + " proceedings");
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public List<Proceeding> getProceedings() {
        return proceedings;
    }
}
