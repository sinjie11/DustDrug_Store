package edu.android.dustdrug;

import android.os.StrictMode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class GetOpenApi {

    private OpenApi openApi = OpenApi.getInstance();
    private LocationFragment locationFragment = new LocationFragment();
    private String sidoName = null, sggName = null, umdName = null;
    private boolean inItem = false, inSidoName = false, inSggName = false, inUmdName = false;

    public String getUmdName() {
        return locationFragment.editText.getText().toString();
    }


    private String AUTHENTICATION_KEY = "2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D";
    URL url;

    {
        try {
            url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?umdName="
                    + getUmdName() + "&pageNo=1&numOfRows=10&ServiceKey=2WjM1G6ETI%2F3HKoHrAC9MhjgY3PufrijH35VWAgVnh3A5ZrEkBkXovDVizsiQoKm7FDHO2AmW4LG%2FA2oiF8new%3D%3D");
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();
            parser.setInput(url.openStream(), null);
            int parserEvent = parser.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("sidoName"))
                            inSidoName = true;
                        if (parser.getName().equals("sggName"))
                            inSggName = true;
                        if (parser.getName().equals("umdName"))
                            inUmdName = true;
                        break;

                    case XmlPullParser.TEXT:
                        if(inSidoName) {
                            sidoName = parser.getText().toString();
                            inSidoName = false;
                        }
                        if(inSggName) {
                            sggName = parser.getText().toString();
                            inSggName = false;
                        }
                        if(inUmdName) {
                            umdName = parser.getText().toString();
                            inUmdName = false;
                        }
                        break;
                    case XmlPullParser.END_TAG :
                        if(parser.getName().equals("item")) {
                            locationFragment.textView.setText(sidoName + "\n" + sggName + "\n" + umdName);
                            inItem = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            locationFragment.textView.setText("Error!!");
        }
    }

}
