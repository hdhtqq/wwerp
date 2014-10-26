package cn.wwerp;

import java.sql.Timestamp;

public class Item extends BaseBean {
	public int Id;
	public String ItemDate;
	public float PrepareAmount;
	public float TotalIncoming;
	public float TotalOutgoing;
	public float RemainAmount;
	public String IncomingRemark = "";
	public String OutgoingRemark = "";
    public Timestamp CreateTime;
	public Timestamp Ts;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getItemDate() {
		return ItemDate;
	}
	public void setItemDate(String itemDate) {
		ItemDate = itemDate;
	}
	public float getPrepareAmount() {
		return PrepareAmount;
	}
	public void setPrepareAmount(float prepareAmount) {
		PrepareAmount = prepareAmount;
	}
	public float getTotalIncoming() {
		return TotalIncoming;
	}
	public void setTotalIncoming(float totalIncoming) {
		TotalIncoming = totalIncoming;
	}
	public float getTotalOutgoing() {
		return TotalOutgoing;
	}
	public void setTotalOutgoing(float totalOutgoing) {
		TotalOutgoing = totalOutgoing;
	}
	public float getRemainAmount() {
		return RemainAmount;
	}
	public void setRemainAmount(float remainAmount) {
		RemainAmount = remainAmount;
	}
	public String getIncomingRemark() {
		return IncomingRemark;
	}
	public void setIncomingRemark(String incomingRemark) {
		IncomingRemark = incomingRemark;
	}
	public String getOutgoingRemark() {
		return OutgoingRemark;
	}
	public void setOutgoingRemark(String outgoingRemark) {
		OutgoingRemark = outgoingRemark;
	}
	public Timestamp getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Timestamp createTime) {
		CreateTime = createTime;
	}
	public Timestamp getTs() {
		return Ts;
	}
	public void setTs(Timestamp ts) {
		Ts = ts;
	}
}
