package uy.edu.ucu.android.parser.model;

import java.util.ArrayList;
import java.util.List;

/**
 * IMPORTANT! YOU SHOULD NOT CHANGE THIS CLASS
 */
public class Proceeding {

    private String id;
    private String url;
    private String title;
    private String description;
    private String onlineAccess;
    private String requisites;
    private String process;
    private String mail;
    private String status;

    private List<Category> categories = new ArrayList<>();
    private List<Link> links = new ArrayList<>();

    private Dependence dependence;
    private WhenAndWhere whenAndWhere;
    private TakeIntoAccount takeIntoAccount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOnlineAccess() {
        return onlineAccess;
    }

    public void setOnlineAccess(String onlineAccess) {
        this.onlineAccess = onlineAccess;
    }

    public String getRequisites() {
        return requisites;
    }

    public void setRequisites(String requisites) {
        this.requisites = requisites;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Dependence getDependence() {
        return dependence;
    }

    public void setDependence(Dependence dependence) {
        this.dependence = dependence;
    }

    public WhenAndWhere getWhenAndWhere() {
        return whenAndWhere;
    }

    public void setWhenAndWhere(WhenAndWhere whenAndWhere) {
        this.whenAndWhere = whenAndWhere;
    }

    public TakeIntoAccount getTakeIntoAccount() {
        return takeIntoAccount;
    }

    public void setTakeIntoAccount(TakeIntoAccount takeIntoAccount) {
        this.takeIntoAccount = takeIntoAccount;
    }
}