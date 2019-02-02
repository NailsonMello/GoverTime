package lass.govertime.Adapter;

/**
 * Created by Nailson on 13/05/2018.
 */

public class ComentObject {

    private String nomeComent;
    private String txtComent;
    private String imgComent;
    private String idUserComent;
    private String dataComent;

    public ComentObject() {
    }

    public ComentObject(String nomeComent, String txtComent, String imgComent, String idUserComent, String dataComent) {
        this.nomeComent = nomeComent;
        this.txtComent = txtComent;
        this.imgComent = imgComent;
        this.idUserComent = idUserComent;
        this.dataComent = dataComent;
    }

    public String getNomeComent() {
        return nomeComent;
    }

    public void setNomeComent(String nomeComent) {
        this.nomeComent = nomeComent;
    }

    public String getTxtComent() {
        return txtComent;
    }

    public void setTxtComent(String txtComent) {
        this.txtComent = txtComent;
    }

    public String getImgComent() {
        return imgComent;
    }

    public void setImgComent(String imgComent) {
        this.imgComent = imgComent;
    }

    public String getIdUserComent() {
        return idUserComent;
    }

    public void setIdUserComent(String idUserComent) {
        this.idUserComent = idUserComent;
    }

    public String getDataComent() {
        return dataComent;
    }

    public void setDataComent(String dataComent) {
        this.dataComent = dataComent;
    }
}