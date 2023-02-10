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
	public ObjectUpdate(String objectId, String objectNamespace, String fieldName, String jsonData) {
		this.objectId = objectId;
		this.objectNamespace = objectNamespace;
		this.fieldName = fieldName;
		this.jsonData = jsonData;
	}

	@Override
	public String toString() {
		return "ObjectUpdate [objectId=" + objectId + ", objectNamespace=" + objectNamespace + ", fieldName=" + fieldName
				+ ", jsonData=" + jsonData + "]";
	}

}
