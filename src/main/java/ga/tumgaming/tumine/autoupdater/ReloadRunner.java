package ga.tumgaming.tumine.autoupdater;

import ga.tumgaming.tumine.TUMain;

public class ReloadRunner implements Runnable {

        private boolean reload;

        public ReloadRunner() {
            this.reload = true;
        }

        @Override
        public void run() {
            if(reload) TUMain.getPlugin().getServer().reload();
            else System.out.println("Reload was cancelled!");
        }

        public void setReload(boolean r) {
            this.reload = r;
        }

}
