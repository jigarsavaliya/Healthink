package com.js.stepcounter.model;


public class DashboardComponentModel {
    Integer ComponentOrder;
    String ComponentName;
    Boolean ShowonDashboard;

    public Integer getComponentOrder() {
        return ComponentOrder;
    }

    public void setComponentOrder(Integer componentOrder) {
        ComponentOrder = componentOrder;
    }

    public String getComponentName() {
        return ComponentName;
    }

    public void setComponentName(String componentName) {
        ComponentName = componentName;
    }

    public Boolean getShowonDashboard() {
        return ShowonDashboard;
    }

    public void setShowonDashboard(Boolean showonDashboard) {
        ShowonDashboard = showonDashboard;
    }
}
