package abkabk.azbarkon.app.features.home

sealed class SliderPage {
    data object BeytOfDay : SliderPage()
    data object Challenge : SliderPage()
    data object TasvirNegar : SliderPage()
}