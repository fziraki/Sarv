import UIKit
import SwiftUI
import Shared

enum SplashStyle {
    static let background = Color(
        red: 108.0 / 255.0,
        green: 77.0 / 255.0,
        blue: 54.0 / 255.0
    )
    static let uiBackground = UIColor(
        red: 108.0 / 255.0,
        green: 77.0 / 255.0,
        blue: 54.0 / 255.0,
        alpha: 1.0
    )
    static let logoSize: CGFloat = 180
    static let minimumDuration: TimeInterval = 1.0
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()
        controller.view.backgroundColor = SplashStyle.uiBackground
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

private struct SplashView: View {
    var body: some View {
        SplashStyle.background
            .overlay {
                Image("LaunchLogo")
                    .resizable()
                    .scaledToFill()
                    .frame(width: SplashStyle.logoSize, height: SplashStyle.logoSize)
                    .clipShape(Circle())
            }
    }
}

struct ContentView: View {
    @State private var showSplash = true

    var body: some View {
        ZStack {
            if showSplash {
                SplashView()
            } else {
                ComposeView()
            }
        }
        .background(SplashStyle.background)
        .ignoresSafeArea()
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + SplashStyle.minimumDuration) {
                withAnimation(.easeOut(duration: 0.2)) {
                    showSplash = false
                }
            }
        }
    }
}
