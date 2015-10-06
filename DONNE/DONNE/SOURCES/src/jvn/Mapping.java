package jvn;

public class Mapping {
	
	private int joi;
	private String jon;
	private JvnObject jo; 
	private JvnRemoteServer js;
	
	
	public Mapping(int joi, String jon, JvnObject jo, JvnRemoteServer js) {
		this.joi = joi;
		this.jon = jon;
		this.jo = jo;
		this.js = js;
	}
	public JvnObject getJo() {
		return jo;
	}
	public void setJo(JvnObject jo) {
		this.jo = jo;
	}
	public int getJoi() {
		return joi;
	}
	public void setJoi(int joi) {
		this.joi = joi;
	}
	public String getJon() {
		return jon;
	}
	public void setJon(String jon) {
		this.jon = jon;
	}
	public JvnRemoteServer getJs() {
		return js;
	}
	public void setJs(JvnRemoteServer js) {
		this.js = js;
	}
	
}
