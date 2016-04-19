(ns salava.badge.ui.routes
  (:require [salava.core.ui.layout :as layout]
            [salava.core.ui.helper :refer [base-path]]
            [salava.badge.ui.my :as my]
            [salava.badge.ui.info :as info]
            [salava.badge.ui.importer :as imp]
            [salava.badge.ui.exporter :as exp]
            [salava.badge.ui.upload :as up]
            [salava.badge.ui.stats :as stats]
            [salava.core.i18n :as i18n :refer [t]]))

(defn placeholder [content]
  (fn [site-navi params]
    #(layout/default site-navi content)))


(defn ^:export routes [context]
  {(str (base-path context) "/badge") [["" my/handler]
                                      ["/mybadges" my/handler]
                                      [["/info/" :badge-id] info/handler]
                                      ["/import" imp/handler]
                                      ["/upload" up/handler]
                                      ["/export" exp/handler]
                                      ["/stats" stats/handler]]})

(defn ^:export navi [context]
  {(str (base-path context) "/badge") {:weight 20 :title (t :badge/Badges)   :top-navi true  :breadcrumb (t :badge/Badges " / " :badge/Mybadges)}
   (str (base-path context) "/badge/mybadges") {:weight 20 :title (t :badge/Mybadges) :site-navi true :breadcrumb (t :badge/Badges " / "  :badge/Mybadges)}
   (str (base-path context) "/badge/import") {:weight 21 :title (t :badge/Import)   :site-navi true :breadcrumb (t :badge/Badges " / "  :badge/Import)}
   (str (base-path context) "/badge/upload") {:weight 22 :title (t :badge/Upload)   :site-navi true :breadcrumb (t :badge/Badges " / "  :badge/Upload)}
   (str (base-path context) "/badge/export") {:weight 23 :title (t :badge/Export)   :site-navi true :breadcrumb (t :badge/Badges " / "  :badge/Export)}
   (str (base-path context) "/badge/stats") {:weight 24 :title (t :badge/Stats)    :site-navi true :breadcrumb (t :badge/Badges " / " :badge/Stats)}
   (str (base-path context) "/badge/info/\\d+") {:breadcrumb   (t :badge/Badges " / " :badge/Badgeinfo)}})

