package admin.perfect.trader.Model;

public class PositionalModel {

    private String sDate,eDate,cName,target1,target2,stoploss,t1,t2,sl,uid,callType,status;

    public PositionalModel(String sDate, String eDate, String cName, String target1,
                           String target2, String stoploss, String t1, String t2, String sl, String uid, String callType,String status) {
        this.sDate = sDate;
        this.eDate = eDate;
        this.cName = cName;
        this.target1 = target1;
        this.target2 = target2;
        this.stoploss = stoploss;
        this.t1 = t1;
        this.t2 = t2;
        this.sl = sl;
        this.uid = uid;
        this.callType = callType;
        this.status = status;
    }

    public PositionalModel() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
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

    public String getStoploss() {
        return stoploss;
    }

    public void setStoploss(String stoploss) {
        this.stoploss = stoploss;
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

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
