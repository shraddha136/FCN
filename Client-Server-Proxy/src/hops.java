
public class hops {

	public static int extracthop(String message) {
		System.out.println("message in extract hop " + message);
		if (message.contains("\\s+")) {
			System.out.println("space");
			message = message.replaceAll("\\s+", "");

		}
		int i = message.indexOf("hop") + 4;
		System.out.println(message.substring(i, message.length()));
		// System.out.println("hop " + Integer.parseInt(message.substring(i,
		// message.length())));
		return Integer.parseInt(message.substring(i, message.length()));
	}

}
