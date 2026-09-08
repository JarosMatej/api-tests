package config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParamsConfig {
    private final List<String> params_names;
    private final List<List<String>> params_vals;

    private List<String> createNames(String[] names) {
        return Arrays.asList(names);
    }

    private List<List<String>> createVals(String[][] vals) {
        List<List<String>> res = new ArrayList<>();

        for (String[] val : vals) {
            res.add(Arrays.asList(val));
        }

        return res;
    }

    public ParamsConfig(String[] names, String[][] vals) {
        params_names = createNames(names);
        params_vals = createVals(vals);
    }

    public ParamsConfig(List<String> names, List<List<String>> vals) {
        params_names = names;
        params_vals = vals;
    }

    public List<String> names() {
        return params_names;
    }

    public List<List<String>> vals() {
        return params_vals;
    }
}
