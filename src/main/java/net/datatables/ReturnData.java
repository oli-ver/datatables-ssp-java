package net.datatables;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Once DataTables has made a request for data, with the parameters sent to the
 * server({@link SentParameters}), it expects JSON data to be returned to it,
 * with the parameters of this class.
 * 
 * 
 * @author oliver
 * @see <a href=
 *      "https://datatables.net/manual/server-side#Returned-data">https://datatables.net/manual/server-side#Returned-data</a>
 */
public class ReturnData {

	private ObjectMapper om = new ObjectMapper();

	/**
	 * The draw counter that this object is a response to - from the <b>draw</b>
	 * parameter sent as part of the data request. Note that it is strongly
	 * recommended for security reasons that you cast this parameter to an
	 * integer, rather than simply echoing back to the client what it sent in
	 * the draw parameter, in order to prevent Cross Site Scripting (XSS)
	 * attacks.
	 */
	protected int draw;

	/**
	 * Total records, before filtering (i.e. the total number of records in the
	 * database)
	 */
	protected int recordsTotal;

	/**
	 * Total records, after filtering (i.e. the total number of records after
	 * filtering has been applied - not just the number of records being
	 * returned for this page of data).
	 */
	protected int recordsFiltered;

	/**
	 * The data to be displayed in the table. This is an array of data source
	 * objects, one for each row, which will be used by DataTables. Note that
	 * this parameter's name can be changed using the <b>ajax</b> option's
	 * <b>dataSrc</b> property.
	 */
	protected List<String[]> data;

	/**
	 * Optional: If an error occurs during the running of the server-side
	 * processing script, you can inform the user of this error by passing back
	 * the error message to be displayed using this parameter. Do not include if
	 * there is no error.
	 */
	protected String error;

	/**
	 * Instantiates a new return data.
	 *
	 * @param draw
	 *            the {@link #draw}
	 * @param recordsTotal
	 *            the {@link #recordsTotal}
	 * @param recordsFiltered
	 *            the {@link #recordsFiltered}
	 * @param data
	 *            the {@link #data}
	 * @param error
	 *            the {@link #error}
	 */
	public ReturnData(int draw, int recordsTotal, int recordsFiltered, List<String[]> data, String error) {
		super();
		this.draw = draw;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.data = data;
		this.error = error;
	}

	/**
	 * Converts the data to return to json.
	 *
	 * @return representation of the class {@link ReturnData} in json
	 * @throws JsonProcessingException
	 *             the json processing exception
	 */
	public String toJson() throws JsonProcessingException {
		return om.writeValueAsString(this);
	}

	/**
	 * Gets the draw counter that this object is a response to - from the
	 * <b>draw</b> parameter sent as part of the data request. Note that it is
	 * strongly recommended for security reasons that you cast this parameter to
	 * an integer, rather than simply echoing back to the client what it sent in
	 * the draw parameter, in order to prevent Cross Site Scripting (XSS)
	 * attacks.
	 *
	 * @return the draw counter that this object is a response to - from the
	 *         <b>draw</b> parameter sent as part of the data request
	 */
	public int getDraw() {
		return draw;
	}

	/**
	 * Gets the total records, before filtering (i.e. the total number of
	 * records in the database).
	 *
	 * @return the recordsTotal
	 */
	public int getRecordsTotal() {
		return recordsTotal;
	}

	/**
	 * Gets the total records, after filtering (i.e. the total number of records
	 * after filtering has been applied - not just the number of records being
	 * returned for this page of data).
	 *
	 * @return the recordsFiltered
	 */
	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	/**
	 * Gets the data to be displayed in the table. This is an array of data
	 * source objects, one for each row, which will be used by DataTables. Note
	 * that this parameter's name can be changed using the <b>ajax</b> option's
	 * <b>dataSrc</b> property.
	 *
	 * @return the data
	 */
	public List<String[]> getData() {
		return data;
	}

	/**
	 * Gets the optional: If an error occurs during the running of the
	 * server-side processing script, you can inform the user of this error by
	 * passing back the error message to be displayed using this parameter. Do
	 * not include if there is no error.
	 *
	 * @return the error
	 */
	public String getError() {
		return error;
	}
}
