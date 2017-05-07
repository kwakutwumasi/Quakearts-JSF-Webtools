package com.quakearts.syshub.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="max_id", schema="dbo")
public class MaxID implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -671685671840967969L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(nullable=false, length=50)
	private String maxIDName;
	@Column(nullable=false, length=50)
	private long maxIDValue;

	public MaxID() {
	}

	public MaxID(String maxPtidName) {
		this.maxIDName = maxPtidName;
	}

	public MaxID(String maxIDName, long maxIDValue) {
		this.maxIDName = maxIDName;
		this.maxIDValue = maxIDValue;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getMaxIDName() {
		return maxIDName;
	}

	public void setMaxIDName(String maxIDName) {
		this.maxIDName = maxIDName;
	}

	public long getMaxIDValue() {
		return maxIDValue;
	}

	public void setMaxIDValue(long maxIDValue) {
		this.maxIDValue = maxIDValue;
	}

}
