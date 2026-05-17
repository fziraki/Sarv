import SwiftUI

@main
struct iOSApp: App {

    init() {
        KoinKt.KoinInit().init()
        Napier.base(DebugAntilog())
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}