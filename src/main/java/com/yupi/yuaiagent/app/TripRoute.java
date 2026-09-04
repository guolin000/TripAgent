package com.yupi.yuaiagent.app;

import java.util.List;

public record TripRoute(List<TripRouteType> routes,
                        String reason) {
}
