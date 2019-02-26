package ru.jackK.repairEAS.client.util.threadWorker;

public enum StateService {
    RUN {
        public String toString() {
            return "Работает";
        }
    },
    STOP {
        public String toString() {
            return "Остановлено";
        }
    },
    PAUSE {
        public String toString() {
            return "Приостановлено";
        }
    },
    CONTINUE {
        public String toString() {
            return "Работает";
        }
    },
    RESTART {
        public String toString() {
            return "";
        }
    },
    STATE {
        public String toString() {return ""; }
    },
    DELETE {
        public String toString() {
            return "";
        }
    },
    INSTALL {
        public String toString() {
            return "";
        }
    }
}
