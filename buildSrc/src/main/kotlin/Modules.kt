object Modules {

    const val APP = ":app"
    const val DOMAIN = ":domain"
    const val DATA = ":data"

    object Base {
        private const val MODULE_NAME = ":base"
        const val CORE = "$MODULE_NAME:core"
        const val EXTENSIONS = "$MODULE_NAME:extensions"
        const val THEME = "$MODULE_NAME:theme"

        object Navigation {
            private const val SUB_MODULE_NAME = "$MODULE_NAME:navigation"
            const val INTERNAL = "$SUB_MODULE_NAME:internal"
            const val API = "$SUB_MODULE_NAME:api"
        }
    }

    object Features {
        private const val MODULE_NAME = ":features"
        const val CONNECTION = "$MODULE_NAME:connection"
    }
}
