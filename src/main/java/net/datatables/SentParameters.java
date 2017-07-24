package net.datatables;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The class {@link SentParameters} holds all parameters Datatables will send to
 * the server. All descriptions of the fields in the class are the work of
 * <a href=
 * "https://datatables.net/manual/server-side">https://datatables.net/manual/server-side</a>
 * and not of the author of this library.
 * 
 * When making a request to the server using server-side processing, DataTables
 * will send the following data in order to let the server know what data is
 * required.
 *
 * @author oliver
 * @see <a href=
 *      "https://datatables.net/manual/server-side">https://datatables.net/manual/server-side</a>
 */
public class SentParameters {

	/** The Constant PARAMETER_NAME_START. */
	private final static String PARAMETER_NAME_START = "start";

	/** The Constant PARAMETER_NAME_LENGTH. */
	private final static String PARAMETER_NAME_LENGTH = "length";

	/** The Constant PARAMETER_NAME_DRAW. */
	private final static String PARAMETER_NAME_DRAW = "draw";

	/** The Constant PARAMETER_NAME_SEARCH_VALUE. */
	private final static String PARAMETER_NAME_SEARCH_VALUE = "search[value]";

	/** The Constant PARAMETER_NAME_COLUMN_SEARCH_VALUE_REGEX. */
	private final static String PARAMETER_NAME_COLUMN_SEARCH_VALUE_REGEX = "columns\\[\\d+\\]\\[search\\]\\[value\\]";

	/** The Constant PARAMETER_NAME_COLUMN_SEARCH_REGEX_REGEX. */
	private final static String PARAMETER_NAME_COLUMN_SEARCH_REGEX_REGEX = "columns\\[\\d+\\]\\[search\\]\\[regex\\]";

	/** The Constant PARAMETER_NAME_COLUMN_SEARCH_ORDERABLE_REGEX. */
	private final static String PARAMETER_NAME_COLUMN_SEARCH_ORDERABLE_REGEX = "columns\\[\\d+\\]\\[orderable\\]";

	/** The Constant PARAMETER_NAME_COLUMN_DATA_REGEX. */
	private final static String PARAMETER_NAME_COLUMN_DATA_REGEX = "columns\\[\\d+\\]\\[data\\]";

	/** The Constant PARAMETER_NAME_ORDER_DIR_REGEX. */
	private final static String PARAMETER_NAME_ORDER_DIR_REGEX = "order\\[\\d+\\]\\[dir\\]";

	/** The Constant PARAMETER_NAME_COLUMNS_NAME_REGEX. */
	private final static String PARAMETER_NAME_COLUMNS_NAME_REGEX = "columns\\[\\d+\\]\\[name\\]";

	/** The Constant PARAMETER_NAME_COLUMNS_SEARCHABLE_REGEX. */
	private final static String PARAMETER_NAME_COLUMNS_SEARCHABLE_REGEX = "columns\\[\\d+\\]\\[searchable\\]";

	/** The Constant REGEX_DECIMAL. */
	private final static String REGEX_DECIMAL = "(\\d+)";

	/** The om. */
	private static ObjectMapper om = new ObjectMapper();

	/**
	 * Draw counter. This is used by DataTables to ensure that the Ajax returns
	 * from server-side processing requests are drawn in sequence by DataTables
	 * (Ajax requests are asynchronous and thus can return out of sequence).
	 * This is used as part of the draw return parameter (see below).
	 * 
	 */
	protected int draw;

	/**
	 * Paging first record indicator. This is the start point in the current
	 * data set (0 index based - i.e. 0 is the first record).
	 * 
	 */
	protected int start;

	/**
	 * Number of records that the table can display in the current draw. It is
	 * expected that the number of records returned will be equal to this
	 * number, unless the server has fewer records to return. Note that this can
	 * be -1 to indicate that all records should be returned (although that
	 * negates any benefits of server-side processing!)
	 * 
	 */
	protected int length;

	/**
	 * Global search value. To be applied to all columns which have
	 * <b>searchable</b> as true.
	 *
	 */
	protected String searchValue;

	/**
	 * <b>true</b> if the global filter should be treated as a regular
	 * expression for advanced searching, <b>false</b> otherwise. Note that
	 * normally server-side processing scripts will not perform regular
	 * expression searching for performance reasons on large data sets, but it
	 * is technically possible and at the discretion of your script.
	 */
	protected boolean isRegexSearch;

	/**
	 * Column to which ordering should be applied. This is an index reference to
	 * the columns array of information that is also submitted to the server.
	 */
	protected List<Integer> orderColumn = new Vector<>();

	/**
	 * The Enum Direction.
	 */
	protected enum Direction {

		/** The asc. */
		ASC,
		/** The desc. */
		DESC
	}

	/**
	 * Ordering direction for this column. It will be asc or desc to indicate
	 * ascending ordering or descending ordering, respectively.
	 */
	protected Hashtable<Integer, Direction> orderDirection = new Hashtable<>();

	/**
	 * Column's data source, as defined by <a href=
	 * "https://datatables.net/reference/option/columns.data">columns.data</a>.
	 */
	protected Hashtable<Integer, String> columnsDatasource = new Hashtable<>();

	/**
	 * Column's name, as defined by <a href=
	 * "https://datatables.net/reference/option/columns.name">columns.name</a>.
	 */
	protected Hashtable<Integer, String> columnsName = new Hashtable<>();

	/**
	 * Flag to indicate if this column is searchable (true) or not (false). This
	 * is controlled by <a href=
	 * "https://datatables.net/reference/option/columns.searchable">columns.searchable</a>.
	 */
	protected Hashtable<Integer, Boolean> columnsSearchable = new Hashtable<>();

	/**
	 * Flag to indicate if this column is orderable (true) or not (false). This
	 * is controlled by <a href=
	 * "https://datatables.net/reference/option/columns.orderable">columns.orderable</a>.
	 */
	protected Hashtable<Integer, Boolean> columnsOrderable = new Hashtable<>();

	/**
	 * Search value to apply to this specific column.
	 */
	protected Hashtable<Integer, String> columnsSearchValue = new Hashtable<>();

	/**
	 * Flag to indicate if the search term for this column should be treated as
	 * regular expression (true) or not (false). As with global search, normally
	 * server-side processing scripts will not perform regular expression
	 * searching for performance reasons on large data sets, but it is
	 * technically possible and at the discretion of your script.
	 */
	protected Hashtable<Integer, Boolean> columnsSearchRegex = new Hashtable<>();

	/**
	 * Constructs a {@link SentParameters} object by parsing the given json
	 * string.
	 *
	 * @param json
	 *            data tables parameters in json format
	 * @return the sent parameters
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static SentParameters fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
		return om.readValue(json, SentParameters.class);
	}

	/**
	 * Gets the draw counter. This is used by DataTables to ensure that the Ajax
	 * returns from server-side processing requests are drawn in sequence by
	 * DataTables (Ajax requests are asynchronous and thus can return out of
	 * sequence). This is used as part of the draw return parameter (see below).
	 *
	 * @return the draw
	 */
	public int getDraw() {
		return draw;
	}

	/**
	 * Gets the paging first record indicator. This is the start point in the
	 * current data set (0 index based - i.e. 0 is the first record).
	 *
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Gets the number of records that the table can display in the current
	 * draw. It is expected that the number of records returned will be equal to
	 * this number, unless the server has fewer records to return. Note that
	 * this can be -1 to indicate that all records should be returned (although
	 * that negates any benefits of server-side processing!).
	 *
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Gets the global search value. To be applied to all columns which have
	 * <b>searchable</b> as true.
	 *
	 * @return the searchValue
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * Checks if is regex search.
	 *
	 * @return the isRegexSearch
	 */
	public boolean isRegexSearch() {
		return isRegexSearch;
	}

	/**
	 * Instantiates a new sent parameters.
	 */
	public SentParameters() {
		super();
	}

	/**
	 * From http servlet request.
	 *
	 * @param req
	 *            the req
	 * @return the sent parameters
	 */
	public static SentParameters fromHttpServletRequest(HttpServletRequest req) {
		SentParameters sp = new SentParameters();

		Map<String, String[]> parameters = req.getParameterMap();

		for (String key : parameters.keySet()) {
			String[] valueArray = parameters.get(key);
			switch (key) {
			case PARAMETER_NAME_START:
				String startString = (String) getFirstArrayElement(valueArray);
				Integer startInt = stringToInt(startString);
				sp.setStart(startInt);
				break;
			case PARAMETER_NAME_LENGTH:
				String lengthString = (String) getFirstArrayElement(valueArray);
				Integer lengthInt = stringToInt(lengthString);
				sp.setLength(lengthInt);
				break;
			case PARAMETER_NAME_DRAW:
				String drawString = (String) getFirstArrayElement(valueArray);
				Integer drawInt = stringToInt(drawString);
				sp.setDraw(drawInt);
				break;
			case PARAMETER_NAME_SEARCH_VALUE:
				String searchValue = (String) getFirstArrayElement(valueArray);
				sp.setSearchValue(searchValue);
				break;
			default:
				parseJavaScriptArray(sp, key, valueArray);
				break;
			}
		}
		return sp;
	}

	/**
	 * Parses the java script array.
	 *
	 * @param sp
	 *            the sp
	 * @param key
	 *            the key
	 * @param valueArray
	 *            the value array
	 */
	private static void parseJavaScriptArray(SentParameters sp, String key, String[] valueArray) {

		if (key.matches(PARAMETER_NAME_COLUMN_SEARCH_VALUE_REGEX)) {
			Hashtable<Integer, String> columnsSearchValues = sp.getColumnsSearchValue();
			int targetIndex = findIndexInKey(key);
			columnsSearchValues.put(targetIndex, (String) getFirstArrayElement(valueArray));
		} else if (key.matches(PARAMETER_NAME_COLUMN_SEARCH_REGEX_REGEX)) {
			Hashtable<Integer, Boolean> columnsSearchBooleanValues = sp.getColumnsSearchRegex();
			int targetIndex = findIndexInKey(key);
			String firstElement = (String) getFirstArrayElement(valueArray);
			Boolean elementBool = stringToBoolean(firstElement);
			columnsSearchBooleanValues.put(targetIndex, elementBool);
		} else if (key.matches(PARAMETER_NAME_COLUMN_SEARCH_ORDERABLE_REGEX)) {
			Hashtable<Integer, Boolean> columnsSearchOrderableValues = sp.getColumnsOrderable();
			int targetIndex = findIndexInKey(key);
			String firstElement = (String) getFirstArrayElement(valueArray);
			Boolean elementBool = stringToBoolean(firstElement);
			columnsSearchOrderableValues.put(targetIndex, elementBool);
		} else if (key.matches(PARAMETER_NAME_COLUMN_DATA_REGEX)) {
			Hashtable<Integer, String> columnsSearchOrderableValues = sp.getColumnsDatasource();
			int targetIndex = findIndexInKey(key);
			String firstElement = (String) getFirstArrayElement(valueArray);
			columnsSearchOrderableValues.put(targetIndex, firstElement);
		} else if (key.matches(PARAMETER_NAME_ORDER_DIR_REGEX)) {
			Hashtable<Integer, Direction> columsOrderDirection = sp.getOrderDirection();
			int targetIndex = findIndexInKey(key);
			String firstElement = (String) getFirstArrayElement(valueArray);
			Direction direction = null;
			if (firstElement != null && firstElement.equalsIgnoreCase("desc")) {
				direction = Direction.DESC;
			} else {
				direction = Direction.ASC;
			}
			columsOrderDirection.put(targetIndex, direction);
		} else if (key.matches(PARAMETER_NAME_COLUMNS_NAME_REGEX)) {
			Hashtable<Integer, String> columnsName = sp.getColumnsName();
			int targetIndex = findIndexInKey(key);
			String firstElement = (String) getFirstArrayElement(valueArray);
			columnsName.put(targetIndex, firstElement);
		} else if (key.matches(PARAMETER_NAME_COLUMNS_SEARCHABLE_REGEX)) {
			Hashtable<Integer, Boolean> columnsSearchable = sp.getColumnsSearchable();
			int targetIndex = findIndexInKey(key);
			String firstElement = (String) getFirstArrayElement(valueArray);
			Boolean elementBool = stringToBoolean(firstElement);
			columnsSearchable.put(targetIndex, elementBool);
		}

	}

	/**
	 * String to boolean.
	 *
	 * @param stringBoolean
	 *            the string boolean
	 * @return the boolean
	 */
	private static Boolean stringToBoolean(String stringBoolean) {
		Boolean elementBool = null;
		if (stringBoolean != null) {
			if (stringBoolean.equalsIgnoreCase("true")) {
				elementBool = true;
			} else {
				elementBool = false;
			}
		}
		return elementBool;
	}

	/**
	 * String to int.
	 *
	 * @param stringInteger
	 *            the string integer
	 * @return the integer
	 */
	private static Integer stringToInt(String stringInteger) {
		Integer elementInt = null;
		if (stringInteger != null) {
			try {
				elementInt = Integer.parseInt(stringInteger);
			} catch (Exception e) {
				// ignore
			}
		}
		return elementInt;
	}

	/**
	 * Gets the first array element.
	 *
	 * @param array
	 *            the array
	 * @return the first array element
	 */
	private static Object getFirstArrayElement(Object[] array) {
		if (array != null && array.length > 0) {
			return array[0];
		} else {
			return null;
		}
	}

	/**
	 * Find index in key.
	 *
	 * @param key
	 *            the key
	 * @return the int
	 */
	private static int findIndexInKey(String key) {
		int index = -1;
		Pattern intsOnly = Pattern.compile(REGEX_DECIMAL);
		Matcher makeMatch = intsOnly.matcher(key);
		makeMatch.find();
		String inputInt = makeMatch.group();
		index = Integer.parseInt(inputInt);
		return index;
	}

	/**
	 * Sets the draw counter. This is used by DataTables to ensure that the Ajax
	 * returns from server-side processing requests are drawn in sequence by
	 * DataTables (Ajax requests are asynchronous and thus can return out of
	 * sequence). This is used as part of the draw return parameter (see below).
	 *
	 * @param draw
	 *            the draw to set
	 */
	public void setDraw(int draw) {
		this.draw = draw;
	}

	/**
	 * Sets the paging first record indicator. This is the start point in the
	 * current data set (0 index based - i.e. 0 is the first record).
	 *
	 * @param start
	 *            the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Sets the number of records that the table can display in the current
	 * draw. It is expected that the number of records returned will be equal to
	 * this number, unless the server has fewer records to return. Note that
	 * this can be -1 to indicate that all records should be returned (although
	 * that negates any benefits of server-side processing!).
	 *
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Sets the global search value. To be applied to all columns which have
	 * <b>searchable</b> as true.
	 *
	 * @param searchValue
	 *            the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	/**
	 * Sets the regex search.
	 *
	 * @param isRegexSearch
	 *            the isRegexSearch to set
	 */
	public void setRegexSearch(boolean isRegexSearch) {
		this.isRegexSearch = isRegexSearch;
	}

	/**
	 * Gets the column to which ordering should be applied. This is an index
	 * reference to the columns array of information that is also submitted to
	 * the server.
	 *
	 * @return the orderColumn
	 */
	public List<Integer> getOrderColumn() {
		return orderColumn;
	}

	/**
	 * Sets the column to which ordering should be applied. This is an index
	 * reference to the columns array of information that is also submitted to
	 * the server.
	 *
	 * @param orderColumn
	 *            the orderColumn to set
	 */
	public void setOrderColumn(List<Integer> orderColumn) {
		this.orderColumn = orderColumn;
	}

	/**
	 * Gets the ordering direction for this column. It will be asc or desc to
	 * indicate ascending ordering or descending ordering, respectively.
	 *
	 * @return the orderDirection
	 */
	public Hashtable<Integer, Direction> getOrderDirection() {
		return orderDirection;
	}

	/**
	 * Sets the ordering direction for this column. It will be asc or desc to
	 * indicate ascending ordering or descending ordering, respectively.
	 *
	 * @param orderDirection
	 *            the orderDirection to set
	 */
	public void setOrderDirection(Hashtable<Integer, Direction> orderDirection) {
		this.orderDirection = orderDirection;
	}

	/**
	 * Gets the column's data source, as defined by <a href=
	 * "https://datatables.net/reference/option/columns.data">columns.data</a>.
	 *
	 * @return the columnsDatasource
	 */
	public Hashtable<Integer, String> getColumnsDatasource() {
		return columnsDatasource;
	}

	/**
	 * Sets the column's data source, as defined by <a href=
	 * "https://datatables.net/reference/option/columns.data">columns.data</a>.
	 *
	 * @param columnsDatasource
	 *            the columnsDatasource to set
	 */
	public void setColumnsDatasource(Hashtable<Integer, String> columnsDatasource) {
		this.columnsDatasource = columnsDatasource;
	}

	/**
	 * Gets the column's name, as defined by <a href=
	 * "https://datatables.net/reference/option/columns.name">columns.name</a>.
	 *
	 * @return the columnsName
	 */
	public Hashtable<Integer, String> getColumnsName() {
		return columnsName;
	}

	/**
	 * Sets the column's name, as defined by <a href=
	 * "https://datatables.net/reference/option/columns.name">columns.name</a>.
	 *
	 * @param columnsName
	 *            the columnsName to set
	 */
	public void setColumnsName(Hashtable<Integer, String> columnsName) {
		this.columnsName = columnsName;
	}

	/**
	 * Gets the flag to indicate if this column is searchable (true) or not
	 * (false). This is controlled by <a href=
	 * "https://datatables.net/reference/option/columns.searchable">columns.searchable</a>.
	 *
	 * @return the columnsSearchable
	 */
	public Hashtable<Integer, Boolean> getColumnsSearchable() {
		return columnsSearchable;
	}

	/**
	 * Sets the flag to indicate if this column is searchable (true) or not
	 * (false). This is controlled by <a href=
	 * "https://datatables.net/reference/option/columns.searchable">columns.searchable</a>.
	 *
	 * @param columnsSearchable
	 *            the columnsSearchable to set
	 */
	public void setColumnsSearchable(Hashtable<Integer, Boolean> columnsSearchable) {
		this.columnsSearchable = columnsSearchable;
	}

	/**
	 * Gets the flag to indicate if this column is orderable (true) or not
	 * (false). This is controlled by <a href=
	 * "https://datatables.net/reference/option/columns.orderable">columns.orderable</a>.
	 *
	 * @return the columnsOrderable
	 */
	public Hashtable<Integer, Boolean> getColumnsOrderable() {
		return columnsOrderable;
	}

	/**
	 * Sets the flag to indicate if this column is orderable (true) or not
	 * (false). This is controlled by <a href=
	 * "https://datatables.net/reference/option/columns.orderable">columns.orderable</a>.
	 *
	 * @param columnsOrderable
	 *            the columnsOrderable to set
	 */
	public void setColumnsOrderable(Hashtable<Integer, Boolean> columnsOrderable) {
		this.columnsOrderable = columnsOrderable;
	}

	/**
	 * Gets the search value to apply to this specific column.
	 *
	 * @return the columnsSearchValue
	 */
	public Hashtable<Integer, String> getColumnsSearchValue() {
		return columnsSearchValue;
	}

	/**
	 * Sets the search value to apply to this specific column.
	 *
	 * @param columnsSearchValue
	 *            the columnsSearchValue to set
	 */
	public void setColumnsSearchValue(Hashtable<Integer, String> columnsSearchValue) {
		this.columnsSearchValue = columnsSearchValue;
	}

	/**
	 * Gets the flag to indicate if the search term for this column should be
	 * treated as regular expression (true) or not (false). As with global
	 * search, normally server-side processing scripts will not perform regular
	 * expression searching for performance reasons on large data sets, but it
	 * is technically possible and at the discretion of your script.
	 *
	 * @return the columnsSearchRegex
	 */
	public Hashtable<Integer, Boolean> getColumnsSearchRegex() {
		return columnsSearchRegex;
	}

	/**
	 * Sets the flag to indicate if the search term for this column should be
	 * treated as regular expression (true) or not (false). As with global
	 * search, normally server-side processing scripts will not perform regular
	 * expression searching for performance reasons on large data sets, but it
	 * is technically possible and at the discretion of your script.
	 *
	 * @param columnsSearchRegex
	 *            the columnsSearchRegex to set
	 */
	public void setColumnsSearchRegex(Hashtable<Integer, Boolean> columnsSearchRegex) {
		this.columnsSearchRegex = columnsSearchRegex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SentParameters [draw=" + draw + ", start=" + start + ", length=" + length + ", searchValue="
				+ searchValue + ", isRegexSearch=" + isRegexSearch + ", orderColumn=" + orderColumn
				+ ", orderDirection=" + orderDirection + ", columnsDatasource=" + columnsDatasource + ", columnsName="
				+ columnsName + ", columnsSearchable=" + columnsSearchable + ", columnsOrderable=" + columnsOrderable
				+ ", columnsSearchValue=" + columnsSearchValue + ", columnsSearchRegex=" + columnsSearchRegex + "]";
	}

}
