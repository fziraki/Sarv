import SwiftUI
import UIKit

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        UIWindow.appearance().backgroundColor = SplashStyle.uiBackground
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
