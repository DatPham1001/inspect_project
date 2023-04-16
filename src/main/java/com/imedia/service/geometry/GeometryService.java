package com.imedia.service.geometry;

import com.imedia.config.application.AppConfig;
import com.imedia.service.pickupaddress.model.GeometryData;
import com.imedia.util.CallServer;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Log4j2
public class GeometryService {

    public GeometryData getGeometry(String fullAddress) {
        try {
            String url = AppConfig.getInstance().geoUrl + "?address=" + fullAddress.replaceAll(" ", "%20");
            String response = CallServer.getInstance().get(url);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("code") == 0) {
                        if (jsonObject.has("data")) {
                            JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location");
                            BigDecimal lng = data.getBigDecimal("lng");
                            BigDecimal lat = data.getBigDecimal("lat");
                            return new GeometryData(lng.doubleValue(), lat.doubleValue());
                        }
                    }
                } catch (Exception e) {
                    log.info("=======MAPBOX API EXCEPTION======", e);
                }
            }
        } catch (Exception e) {
            log.info("=======MAPBOX API CONFIG EXCEPTION======", e);
        }
        return null;
    }
}
