package jvn;

import java.util.ArrayList;
import java.util.List;

public class CoordStruct {

	private String jon;
	private JvnObject jo;
	private int joi;
	private List<Mapping> server;
	private JvnRemoteServer serverWriter;

	public List<Mapping> getServer() {
		return server;
	}

	public String getJon() {
		return jon;
	}

	public void setJon(String jon) {
		this.jon = jon;
	}

	public int getJoi() {
		return joi;
	}

	public void setJoi(int joi) {
		this.joi = joi;
	}

	public JvnObject getJo() {
		return jo;
	}

	public void setJo(JvnObject jo) {
		this.jo = jo;
	}
	public void setServer(List<Mapping> server) {
		this.server = server;
	}

	public JvnRemoteServer getServerWriter() {
		return serverWriter;
	}

	public void setServerWriter(JvnRemoteServer serverWriter) {
		this.serverWriter = serverWriter;
	}

	public CoordStruct(String nom, JvnObject objet, int joi, List<Mapping> server){
		this.jon = nom;
		this.jo = objet;
		this.joi = joi;
		this.server = server;
		this.serverWriter = null;
	}

}
