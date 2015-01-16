package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Matrix.IMatrix;

import java.util.Map;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public class MatrixEntry implements Map.Entry<String, IMatrix> {

    private String key;

    private IMatrix value;

    public MatrixEntry(String key, IMatrix value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public IMatrix getValue() {
        return value;
    }

    @Override
    public IMatrix setValue(IMatrix value) {
        this.value = value;
        return value;
    }
}
