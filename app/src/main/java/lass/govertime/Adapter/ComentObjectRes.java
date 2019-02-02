package lass.govertime.Adapter;

/**
 * Created by Nailson on 20/05/2018.
 */

public class ComentObjectRes {

    private String nomeComentRes;
    private String txtComentRes;
    private String imgComentRes;
    private String idUserComentRes;
    private String dataComentRes;
    private String idComentRes;

    public ComentObjectRes() {
    }

    public ComentObjectRes(String nomeComentRes, String txtComentRes, String imgComentRes, String idUserComentRes, String dataComentRes, String idComentRes) {
        this.nomeComentRes = nomeComentRes;
        this.txtComentRes = txtComentRes;
        this.imgComentRes = imgComentRes;
        this.idUserComentRes = idUserComentRes;
        this.dataComentRes = dataComentRes;
        this.idComentRes = idComentRes;
    }

    public String getNomeComentRes() {
        return nomeComentRes;
    }

    public void setNomeComentRes(String nomeComentRes) {
        this.nomeComentRes = nomeComentRes;
    }

    public String getTxtComentRes() {
        return txtComentRes;
    }

    public void setTxtComentRes(String txtComentRes) {
        this.txtComentRes = txtComentRes;
    }

    public String getImgComentRes() {
        return imgComentRes;
    }

    public void setImgComentRes(String imgComentRes) {
        this.imgComentRes = imgComentRes;
    }

    public String getIdUserComentRes() {
        return idUserComentRes;
    }

    public void setIdUserComentRes(String idUserComentRes) {
        this.idUserComentRes = idUserComentRes;
    }

    public String getDataComentRes() {
        return dataComentRes;
    }

    public void setDataComentRes(String dataComentRes) {
        this.dataComentRes = dataComentRes;
    }

    public String getIdComentRes() {
        return idComentRes;
    }

    public void setIdComentRes(String idComentRes) {
        this.idComentRes = idComentRes;
    }
}
