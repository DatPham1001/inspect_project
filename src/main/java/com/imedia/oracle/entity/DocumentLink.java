package com.imedia.oracle.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the DOCUMENT_LINKS database table.
 * 
 */
@Entity
@Table(name="DOCUMENT_LINKS")
public class DocumentLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="PATH_TOPIC")
	private String pathTopic;

	@Column(name="TOPIC_ID")
	private BigDecimal topicId;

	@Column(name="TOPIC_NAME")
	private String topicName;

	private Timestamp utimestamp;

	public DocumentLink() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPathTopic() {
		return this.pathTopic;
	}

	public void setPathTopic(String pathTopic) {
		this.pathTopic = pathTopic;
	}

	public BigDecimal getTopicId() {
		return this.topicId;
	}

	public void setTopicId(BigDecimal topicId) {
		this.topicId = topicId;
	}

	public String getTopicName() {
		return this.topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public Timestamp getUtimestamp() {
		return this.utimestamp;
	}

	public void setUtimestamp(Timestamp utimestamp) {
		this.utimestamp = utimestamp;
	}

}