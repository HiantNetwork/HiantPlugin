package org.insilicon.hiantplugin.CustomClasses.configClasses.RegenConfigs;

public class RegenConfigRoot {
    private Regeneration regeneration;

    // Standard getters and setters for RegenConfigRoot
    public Regeneration getRegeneration() {
        return regeneration;
    }

    public void setRegeneration(Regeneration regeneration) {
        this.regeneration = regeneration;
    }

    // Nested Regeneration class
    public static class Regeneration {
        private boolean enabled;
        private int interval;
        private Area grass;
        private Area caves;
        private Area deepcaves;

        // Standard getters and setters for Regeneration
        // ...
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public Area getGrass() {
            return grass;
        }

        public void setGrass(Area grass) {
            this.grass = grass;
        }

        public Area getCaves() {
            return caves;
        }

        public void setCaves(Area caves) {
            this.caves = caves;
        }

        public Area getDeepcaves() {
            return deepcaves;
        }

        public void setDeepcaves(Area deepcaves) {
            this.deepcaves = deepcaves;
        }

    }

    // Nested Area class
    public static class Area {
        private int xA;
        private int yA;
        private int zA;
        private int xB;
        private int yB;
        private int zB;

        // Standard getters and setters for Area
        // ...

        // Inside RegenConfigRoot.Area class

        public int getXA() {
            return xA;
        }

        public void setXA(int xA) {
            this.xA = xA;
        }

        public int getYA() {
            return yA;
        }

        public void setYA(int yA) {
            this.yA = yA;
        }

        public int getZA() {
            return zA;
        }

        public void setZA(int zA) {
            this.zA = zA;
        }

        public int getXB() {
            return xB;
        }

        public void setXB(int xB) {
            this.xB = xB;
        }

        public int getYB() {
            return yB;
        }

        public void setYB(int yB) {
            this.yB = yB;
        }

        public int getZB() {
            return zB;
        }

        public void setZB(int zB) {
            this.zB = zB;
        }


    }
}
