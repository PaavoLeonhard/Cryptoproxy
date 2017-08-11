package JavaServer.MyMiddleware;

/**
 * Wrapper class for a String since Spring-Boot sends data as a JSON
 * @author Paavo.Camps
 */
public class Data {
	private String data;

	public Data(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
