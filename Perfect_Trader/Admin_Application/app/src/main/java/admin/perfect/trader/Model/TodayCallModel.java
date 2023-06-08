package admin.perfect.trader.Model;

public class TodayCallModel {

    private String c_name, target1, target2, stop_loss, call_type, uid, time,t1,t2,sl;

    public TodayCallModel() {
    }

    public TodayCallModel(String c_name, String target1, String target2, String stop_loss, String call_type, String uid, String time, String t1, String t2, String sl) {
        this.c_name = c_name;
        this.target1 = target1;
        this.target2 = target2;
        this.stop_loss = stop_loss;
        this.call_type = call_type;
        this.uid = uid;
        this.time = time;
        this.t1 = t1;
        this.t2 = t2;
        this.sl = sl;

    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    public String getT2() {
        return t2;
    }

    public void setT2(String t2) {
        this.t2 = t2;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getTarget1() {
        return target1;
    }

    public void setTarget1(String target1) {
        this.target1 = target1;
    }

    public String getTarget2() {
        return target2;
    }

    public void setTarget2(String target2) {
        this.target2 = target2;
    }

    public String getStop_loss() {
        return stop_loss;
    }

    public void setStop_loss(String stop_loss) {
        this.stop_loss = stop_loss;
    }

    public String getCall_type() {
        return call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
