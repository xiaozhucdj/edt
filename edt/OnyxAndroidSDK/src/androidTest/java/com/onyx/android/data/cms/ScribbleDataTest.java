package com.onyx.android.data.cms;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
import com.onyx.android.sdk.data.cms.OnyxScribble;
import com.onyx.android.sdk.data.cms.OnyxScribblePoint;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhuzeng on 7/20/16.
 */
public class ScribbleDataTest extends ApplicationTestCase<Application> {

    public ScribbleDataTest() {
        super(Application.class);
    }

    private OnyxScribblePoint randomPoint() {
        final OnyxScribblePoint point = new OnyxScribblePoint(
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000),
                TestUtils.randInt(0, 1000));
        return point;
    }

    private OnyxScribble randomScribble(final String app, final String md5, final String position) {
        final OnyxScribble scribble = new OnyxScribble();
        scribble.setMD5(md5);
        scribble.setApplication(app);
        scribble.setPosition(position);
        scribble.generateUniqueId();
        int count = TestUtils.randInt(10, 2000);
        final List<OnyxScribblePoint> points = scribble.allocatePoints(count);
        for(int i = 0; i < count; ++i) {
            points.add(randomPoint());
        }
        return scribble;
    }

    public void testSinglePageBulkInsert() {
        final List<OnyxScribble> scribbleList = new ArrayList<OnyxScribble>();
        int limit = TestUtils.randInt(100, 500);
        final String md5 = UUID.randomUUID().toString();
        final String app = UUID.randomUUID().toString();
        final String position = UUID.randomUUID().toString();
        for(int i = 0; i < limit; ++i) {
            scribbleList.add(randomScribble(app, md5, position));
        }
        assertTrue(OnyxCmsCenter.insertScribbleBulk(getContext(),
                getContext().getPackageName(),
                scribbleList));

        List<OnyxScribble> result = new ArrayList<>();
        OnyxCmsCenter.getScribbles(getContext(), app, md5, position, result);
        assertTrue(result.size() == scribbleList.size());

    }
}
