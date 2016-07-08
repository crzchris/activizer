package Tools;

import android.content.Context;

/**
 * Created by CE on 17/5/2016.
 */

public interface AsyncFinishListener {

    void processFinished(Boolean isFinished);

    void processFinished(Context context, Boolean isFinished, String tableName,
                         String keyName, String goodIds);

}