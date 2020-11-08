import org.lightcouch.CouchDbClient;
import org.lightcouch.DesignDocument;
import org.lightcouch.DesignDocument.MapReduce;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        CouchDbClient dbClient = new CouchDbClient();

        MapReduce concatValues = new MapReduce();

        concatValues.setMap("function(doc) { emit(doc.stringField, Array(doc.intField + 1).join(doc.stringField)); }");
        concatValues.setReduce("function (keys, values, rereduce) { return values.join('#'); }");

        Map<String, MapReduce> views = new HashMap<>();
        views.put("get_concatValues", concatValues);

        DesignDocument designDocument = new DesignDocument();
        designDocument.setId("_design/szorgalmi");
        designDocument.setLanguage("javascript");
        designDocument.setViews(views);

        dbClient.design().synchronizeWithDb(designDocument);

        List<ConcatResult> results = dbClient
//                .view("_temp_view")
//                .tempView(concatValues)
                .view("szorgalmi/get_concatValues")
                .group(true)
                .query(ConcatResult.class);

        for (ConcatResult result : results) {
            System.out.println(result);
        }
    }
}
