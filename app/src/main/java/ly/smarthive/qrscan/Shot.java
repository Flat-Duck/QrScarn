package ly.smarthive.qrscan;

public class Shot {

    String number, type,takenAt;

    public Shot() {}

    public Shot(String number, String type, String takenAt) {
        this.number = number;
        this.type = type;
        this.takenAt = takenAt;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(String takenAt) {
        this.takenAt = takenAt;
    }

}
