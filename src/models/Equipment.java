public class Equipment
{
    //Fields the make up the Equipment class
	private String equipName;
    private int equipmentID;
    private String reqSkill;
    private String status;

    //default empty constructor
    public Equipment()
    {
        this.equipName = "Unknown";
        this.equipmentID = 0;
        this.reqSkill = "Unknown";
        this.status = "Invalid";
    }

    //Standard constructor for equipment class
    public Equipment(String equipName, int equipID, String reqSkill, String status)
    {
        this.equipName = equipName;
        this.equipmentID = equipID;
        this.reqSkill = reqSkill;
        this.status = status;
    }

    //Getters and setters
    public String getEquipName() {return equipName;}
    public void setEquipName(String equipName) {
        this.equipName = equipName;}

    public int getEquipmentID() {return equipmentID;}
    public void setEquipmentID(int equipmentID) {
        this.equipmentID = equipmentID;}

    public String getReqSkill() {return reqSkill;}
    public void setReqSkill(String reqSkill) {
        this.reqSkill = reqSkill;}

    public String getStatus() {return status;}
    public void setStatus(String status) {
        this.status = status;}

    //Methods

    //this is a very basic version of this method, later iterations will likely do more than just this
    public updateStatus(String newStatus) {
        setStatus(newStatus)
    }

    //toString method for basic display of object information
    public String toString(){
        return String.format("%s, %d, %s, %s", equipName, equipmentID, reqSkill, status);
    }
}