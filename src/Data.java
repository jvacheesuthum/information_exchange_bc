import java.io.Serializable;

public class Data implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean atomic; //determine whether this data is an atomic of an array 
	private String data; //null if this is an array data
	private String[] arr; //null if this is atomic, can be refs or string data
	
	public Data(String data){
		this.data = data;
		atomic = true;
	}
	
	public Data(String[] arr){
		this.arr = arr;
		atomic = false;
	}
	
	@Override
	public String toString(){
		return atomic? data : printAr(); 
	}

	private String printAr() {
		String ans = "";
		for (String s : arr) {
			ans += " " + s;
		}
		return ans;
	}
}
