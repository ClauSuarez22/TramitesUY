package uy.edu.ucu.android.tramitesuy;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import java.util.List;

import uy.edu.ucu.android.parser.model.Proceeding;
import uy.edu.ucu.android.parser.provider.ProceedingProvider;

/**
 * To test Proceedings import from tramites.xml file
 */
public class ParserTest extends InstrumentationTestCase {

    private static final String TAG = ParserTest.class.getSimpleName();
    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        mContext = getInstrumentation().getTargetContext();
    }

    public void testImportProceedings() throws Exception {
        List<Proceeding> proceedings = ProceedingProvider.getInstance(mContext).getProceedings();
        Log.d(TAG, "Proceedings: " + proceedings.size());
        assertEquals(proceedings.size() > 0, true);

    }

}
