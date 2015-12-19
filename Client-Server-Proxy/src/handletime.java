
//package FCN_PROJ;

import java.text.SimpleDateFormat;

/**
 *
 * @author Shraddha Atrawalkar
 */
public class handletime {

	String returnMessage = null;

	String reqdtime(String number) {
		Long tempdate;
		tempdate = servert.reqt;
		tempdate = serveru.reqt;

		if (number.contains("CAL")) {

			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			System.out.println("data to be converted from " + tempdate + " to cal ");
			returnMessage = sdf.format(tempdate);
		} else if (number.contains("UTC")) {
			returnMessage = String.valueOf(tempdate) + "\n";
		} else if (!number.isEmpty()) {
			// valid client wants to modify time
			synchronized (this) {
				modifynumber(number);
				returnMessage = "Data set to: " + servert.reqt;
				returnMessage = "Data set to: " + serveru.reqt;
			}
		} else {
			returnMessage = String.valueOf(servert.reqt) + "\n";
		}
		return returnMessage;
	}

	private void modifynumber(String number) {
		try {
			servert.reqt = Long.parseLong(number);
			serveru.reqt = Long.parseLong(number);
		} catch (NumberFormatException e) {
		}
	}

}
