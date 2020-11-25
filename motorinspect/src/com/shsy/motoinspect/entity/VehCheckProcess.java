package com.shsy.motoinspect.entity;

import java.util.Date;

public class VehCheckProcess implements Comparable<VehCheckProcess>{
	
	private String jylsh;
	
	private String hphm;
	
	private String hpzl;
	
	private String clsbdh;
	
	private String jyxm;
	
	private Date kssj;
	
	private Date jssj;
	
	private Integer jycs;
	
	private Date jygcxrsj;
	
	private String jysbbh;
	
	//已完成:1;
	//未结束:2
	//未开始:0
	//异常:-1;
	private String jyzt;
	
	private Integer jcxdh;
	
	//视频状态已下载:1
	private Integer voideSate;
	
	
	public VehCheckProcess(){
		
	}
	

	public VehCheckProcess(String jylsh, String hphm, String hpzl, String clsbdh, String jyxm, Date kssj, Date jssj,
			Integer jycs) {
		super();
		this.jylsh = jylsh;
		this.hphm = hphm;
		this.hpzl = hpzl;
		this.clsbdh = clsbdh;
		this.jyxm = jyxm;
		this.kssj = kssj;
		this.jssj = jssj;
		this.jycs = jycs;
	}

	
	public Integer getJcxdh() {
		return jcxdh;
	}

	public void setJcxdh(Integer jcxdh) {
		this.jcxdh = jcxdh;
	}

	public Integer getVoideSate() {
		return voideSate;
	}

	public void setVoideSate(Integer voideSate) {
		this.voideSate = voideSate;
	}

	public Date getKssj() {
		return kssj;
	}

	public Date getJssj() {
		return jssj;
	}

	public Integer getJycs() {
		return jycs;
	}

	public void setKssj(Date kssj) {
		this.kssj = kssj;
	}

	public void setJssj(Date jssj) {
		this.jssj = jssj;
	}

	public void setJycs(Integer jycs) {
		this.jycs = jycs;
	}

	public String getJylsh() {
		return jylsh;
	}

	public String getHphm() {
		return hphm;
	}

	public String getHpzl() {
		return hpzl;
	}

	public String getClsbdh() {
		return clsbdh;
	}

	public String getJyxm() {
		return jyxm;
	}


	public void setJylsh(String jylsh) {
		this.jylsh = jylsh;
	}

	public void setHphm(String hphm) {
		this.hphm = hphm;
	}

	public void setHpzl(String hpzl) {
		this.hpzl = hpzl;
	}

	public void setClsbdh(String clsbdh) {
		this.clsbdh = clsbdh;
	}

	public void setJyxm(String jyxm) {
		this.jyxm = jyxm;
	}


	public String getJysbbh() {
		return jysbbh;
	}

	public String getJyzt() {
		return jyzt;
	}


	public void setJysbbh(String jysbbh) {
		this.jysbbh = jysbbh;
	}

	public void setJyzt(String jyzt) {
		this.jyzt = jyzt;
	}

	public Date getJygcxrsj() {
		return jygcxrsj;
	}

	public void setJygcxrsj(Date jygcxrsj) {
		this.jygcxrsj = jygcxrsj;
	}

	



	@Override
	public int compareTo(VehCheckProcess another) {
		if(this.jycs.compareTo(another.jycs)==0){
			if(this.kssj!=null && another.kssj!=null){
				return this.kssj.compareTo(another.kssj);
			}else{
				return 0;
			}
		}else{
			return this.jycs.compareTo(another.jycs);
		}
	}

}
