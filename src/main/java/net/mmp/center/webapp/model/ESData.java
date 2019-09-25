package net.mmp.center.webapp.model;

public class ESData {

	private String _index;
	private String _type;
	private String _id;
	private double _score;
	private TwampMeasureResult  _source;
	private String[] sort;
	
	
	
	public String get_index() {
		return _index;
	}
	public void set_index(String _index) {
		this._index = _index;
	}
	public String get_type() {
		return _type;
	}
	public void set_type(String _type) {
		this._type = _type;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public double get_score() {
		return _score;
	}
	public void set_score(double _score) {
		this._score = _score;
	}
	public TwampMeasureResult get_source() {
		return _source;
	}
	public void set_source(TwampMeasureResult _source) {
		this._source = _source;
	}
	public String[] getSort() {
		return sort;
	}
	public void setSort(String[] sort) {
		this.sort = sort;
	}
}
