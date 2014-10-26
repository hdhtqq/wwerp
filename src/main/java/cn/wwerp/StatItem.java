package cn.wwerp;

public class StatItem extends BaseBean {
	public String time;
	public int Incoming;
	public int Outgoing;
	public String IncomingDesc;
	public String OutgoingDesc;
	public String getOutgoingDesc() {
		return OutgoingDesc;
	}
	public void setOutgoingDesc(String outgoingDesc) {
		OutgoingDesc = outgoingDesc;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getIncoming() {
		return Incoming;
	}
	public void setIncoming(int incoming) {
		Incoming = incoming;
	}
	public int getOutgoing() {
		return Outgoing;
	}
	public void setOutgoing(int outgoing) {
		Outgoing = outgoing;
	}
	public String getIncomingDesc() {
		return IncomingDesc;
	}
	public void setIncomingDesc(String incomingDesc) {
		IncomingDesc = incomingDesc;
	}
	
}
