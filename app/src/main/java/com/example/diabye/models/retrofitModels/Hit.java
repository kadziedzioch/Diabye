package com.example.diabye.models.retrofitModels;

import java.util.ArrayList;

public class Hit {

    private String _index;
    private String _type;
    private String _id;
    private Object _score;
    private FoodRetrofit fields;
    private ArrayList<Integer> sort;

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

    public Object get_score() {
        return _score;
    }

    public void set_score(Object _score) {
        this._score = _score;
    }

    public FoodRetrofit getFields() {
        return fields;
    }

    public void setFields(FoodRetrofit fields) {
        this.fields = fields;
    }

    public ArrayList<Integer> getSort() {
        return sort;
    }

    public void setSort(ArrayList<Integer> sort) {
        this.sort = sort;
    }

}
