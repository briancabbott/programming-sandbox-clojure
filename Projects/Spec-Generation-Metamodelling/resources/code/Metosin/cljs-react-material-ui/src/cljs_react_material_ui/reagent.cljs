(ns cljs-react-material-ui.reagent
  (:refer-clojure :exclude [list stepper])
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [reagent.impl.template]
            [reagent.interop :refer-macros [$ $!]]))

(def selectable-list (r/adapt-react-class ((aget js/MaterialUI "makeSelectable") (aget js/MaterialUI "List"))))

(def app-bar (r/adapt-react-class (aget js/MaterialUI "AppBar")))
(def auto-complete (r/adapt-react-class (aget js/MaterialUI "AutoComplete")))
(def avatar (r/adapt-react-class (aget js/MaterialUI "Avatar")))
(def badge (r/adapt-react-class (aget js/MaterialUI "Badge")))
(def bottom-navigation (r/adapt-react-class (aget js/MaterialUI "BottomNavigation")))
(def bottom-navigation-item (r/adapt-react-class (aget js/MaterialUI "BottomNavigationItem")))
(def card (r/adapt-react-class (aget js/MaterialUI "Card")))
(def card-actions (r/adapt-react-class (aget js/MaterialUI "CardActions")))
(def card-header (r/adapt-react-class (aget js/MaterialUI "CardHeader")))
(def card-media (r/adapt-react-class (aget js/MaterialUI "CardMedia")))
(def card-title (r/adapt-react-class (aget js/MaterialUI "CardTitle")))
(def card-text (r/adapt-react-class (aget js/MaterialUI "CardText")))
(def checkbox (r/adapt-react-class (aget js/MaterialUI "Checkbox")))
(def chip (r/adapt-react-class (aget js/MaterialUI "Chip")))
(def circular-progress (r/adapt-react-class (aget js/MaterialUI "CircularProgress")))
(def date-picker (r/adapt-react-class (aget js/MaterialUI "DatePicker")))
(def dialog (r/adapt-react-class (aget js/MaterialUI "Dialog")))
(def divider (r/adapt-react-class (aget js/MaterialUI "Divider")))
(def drawer (r/adapt-react-class (aget js/MaterialUI "Drawer")))
(def drop-down-menu (r/adapt-react-class (aget js/MaterialUI "DropDownMenu")))
(def flat-button (r/adapt-react-class (aget js/MaterialUI "FlatButton")))
(def floating-action-button (r/adapt-react-class (aget js/MaterialUI "FloatingActionButton")))
(def font-icon (r/adapt-react-class (aget js/MaterialUI "FontIcon")))
(def grid-list (r/adapt-react-class (aget js/MaterialUI "GridList")))
(def grid-tile (r/adapt-react-class (aget js/MaterialUI "GridTile")))
(def icon-button (r/adapt-react-class (aget js/MaterialUI "IconButton")))
(def icon-menu (r/adapt-react-class (aget js/MaterialUI "IconMenu")))
(def linear-progress (r/adapt-react-class (aget js/MaterialUI "LinearProgress")))
(def list (r/adapt-react-class (aget js/MaterialUI "List")))
(def list-item (r/adapt-react-class (aget js/MaterialUI "ListItem")))
(def menu (r/adapt-react-class (aget js/MaterialUI "Menu")))
(def menu-item (r/adapt-react-class (aget js/MaterialUI "MenuItem")))
(def mui-theme-provider (r/adapt-react-class (aget js/MaterialUI "MuiThemeProvider")))
(def paper (r/adapt-react-class (aget js/MaterialUI "Paper")))
(def popover (r/adapt-react-class (aget js/MaterialUI "Popover")))
(def radio-button (r/adapt-react-class (aget js/MaterialUI "RadioButton")))
(def radio-button-group (r/adapt-react-class (aget js/MaterialUI "RadioButtonGroup")))
(def raised-button (r/adapt-react-class (aget js/MaterialUI "RaisedButton")))
(def refresh-indicator (r/adapt-react-class (aget js/MaterialUI "RefreshIndicator")))
(def select-field (r/adapt-react-class (aget js/MaterialUI "SelectField")))
(def slider (r/adapt-react-class (aget js/MaterialUI "Slider")))
(def subheader (r/adapt-react-class (aget js/MaterialUI "Subheader")))
(def svg-icon (r/adapt-react-class (aget js/MaterialUI "SvgIcon")))
(def step (r/adapt-react-class (aget js/MaterialUI "Step")))
(def step-button (r/adapt-react-class (aget js/MaterialUI "StepButton")))
(def step-content (r/adapt-react-class (aget js/MaterialUI "StepContent")))
(def step-label (r/adapt-react-class (aget js/MaterialUI "StepLabel")))
(def stepper (r/adapt-react-class (aget js/MaterialUI "Stepper")))
(def snackbar (r/adapt-react-class (aget js/MaterialUI "Snackbar")))
(def tabs (r/adapt-react-class (aget js/MaterialUI "Tabs")))
(def tab (r/adapt-react-class (aget js/MaterialUI "Tab")))
(def table (r/adapt-react-class (aget js/MaterialUI "Table")))
(def table-body (r/adapt-react-class (aget js/MaterialUI "TableBody")))
(def table-footer (r/adapt-react-class (aget js/MaterialUI "TableFooter")))
(def table-header (r/adapt-react-class (aget js/MaterialUI "TableHeader")))
(def table-header-column (r/adapt-react-class (aget js/MaterialUI "TableHeaderColumn")))
(def table-row (r/adapt-react-class (aget js/MaterialUI "TableRow")))
(def table-row-column (r/adapt-react-class (aget js/MaterialUI "TableRowColumn")))
(def text-field (r/adapt-react-class (aget js/MaterialUI "TextField")))
(def time-picker (r/adapt-react-class (aget js/MaterialUI "TimePicker")))
(def toggle (r/adapt-react-class (aget js/MaterialUI "Toggle")))
(def toolbar (r/adapt-react-class (aget js/MaterialUI "Toolbar")))
(def toolbar-group (r/adapt-react-class (aget js/MaterialUI "ToolbarGroup")))
(def toolbar-separator (r/adapt-react-class (aget js/MaterialUI "ToolbarSeparator")))
(def toolbar-title (r/adapt-react-class (aget js/MaterialUI "ToolbarTitle")))
