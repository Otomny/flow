package fr.omny.flow.data;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectUpdate {

	private String objectId;
	private String objectNamespace;
	private String fieldName;
	private String jsonData;
	private String serverName;

	/**
	 * 
	 */
	public ObjectUpdate() {}

	/**
	 * @param objectId
	 * @param objectNamespace
	 * @param fieldName
	 * @param jsonData
	 */
	public ObjectUpdate(String objectId, String objectNamespace, String fieldName, String jsonData, String serverName) {
		this.objectId = objectId;
		this.objectNamespace = objectNamespace;
		this.fieldName = fieldName;
		this.jsonData = jsonData;
		this.serverName = serverName;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	@Override
	public String toString() {
		return "ObjectUpdate [objectId=" + objectId + ", objectNamespace=" + objectNamespace + ", fieldName=" + fieldName
				+ ", jsonData=" + jsonData + ", serverName=" + serverName + "]";
	}

}
