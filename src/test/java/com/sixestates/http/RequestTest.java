package com.sixestates.http;
import com.sixestates.exception.ApiException;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.LocalDate;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static com.sixestates.Assert.assertQueryStringsEqual;
import static com.sixestates.Assert.assertUrlsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertThrows;
public class RequestTest {
    @Test
    public void testNoEdgeOrRegionInUrl() throws MalformedURLException {
        final Request request = new Request(HttpMethod.GET, "https://www.google.com");
        assertUrlsEqual(new URL("https://www.google.com"), request.constructURL());
    }
}
