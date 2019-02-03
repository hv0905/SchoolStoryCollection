package net.sakuratrak.schoolstorycollection;

import net.sakuratrak.schoolstorycollection.core.AppHelper;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test_dayDelta() {
        Calendar cd = Calendar.getInstance();
        cd.set(2018, 1, 375);
        int result = AppHelper.getDeltaDay(Calendar.getInstance(), cd);
        assertEquals(result, 8);
    }
}