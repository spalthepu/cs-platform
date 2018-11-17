package ai.customersmart;


public class CSPEvent {

    public String getContentType() {
        return contentType;
    }

    public String getPayLoad() {
        return payLoad;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    String contentType;
    String payLoad;

    public CSPEvent (String content, String type) {
        contentType = type;
        payLoad = content;
    }

    public String toString() {
        return payLoad;
    }

}
