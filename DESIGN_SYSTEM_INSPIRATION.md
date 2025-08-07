import React, { useState, useEffect } from 'react'

export default function FramesDemo() {
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [currentSlide, setCurrentSlide] = useState(0)
  const [showToast, setShowToast] = useState(false)
  const [formData, setFormData] = useState({ email: '', message: '' })
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const slides = [
    { id: 1, title: 'Abstract Waves', category: 'Nature', premium: true },
    { id: 2, title: 'Neon City', category: 'Urban', premium: false },
    { id: 3, title: 'Mountain Mist', category: 'Nature', premium: true },
    { id: 4, title: 'Digital Dreams', category: 'Abstract', premium: false },
  ]

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % slides.length)
  }

  const prevSlide = () => {
    setCurrentSlide((prev) => (prev - 1 + slides.length) % slides.length)
  }

  const handleShowToast = () => {
    setShowToast(true)
    setTimeout(() => setShowToast(false), 5000)
  }

  useEffect(() => {
    const interval = setInterval(nextSlide, 5000)
    return () => clearInterval(interval)
  }, [])

  return (
    <div className="min-h-screen bg-zinc-950">
      {/* Glass Navigation */}
      <header className="fixed top-0 z-50 w-full border-b border-zinc-800 bg-zinc-900/80 backdrop-blur-sm">
        <div className="container mx-auto px-4">
          <nav className="flex h-16 items-center justify-between">
            <div className="flex items-center space-x-8">
              <h1 className="text-2xl font-bold text-white">Frames</h1>
              <div className="hidden md:flex space-x-6">
                <a href="#" className="text-zinc-400 hover:text-white transition-colors">Gallery</a>
                <a href="#" className="text-zinc-400 hover:text-white transition-colors">Collections</a>
                <a href="#" className="text-zinc-400 hover:text-white transition-colors">Premium</a>
                <a href="#" className="text-zinc-400 hover:text-white transition-colors">About</a>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => setIsModalOpen(true)}
                className="px-4 py-2 bg-white/90 text-zinc-900 rounded-lg hover:bg-white/80 transition-colors backdrop-blur-sm font-medium"
              >
                Sign In
              </button>
              <button
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                className="md:hidden p-2 text-zinc-400 hover:text-white"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                </svg>
              </button>
            </div>
          </nav>
        </div>
      </header>

      {/* Mobile Menu */}
      {isMenuOpen && (
        <div className="fixed inset-0 z-40 bg-zinc-900/95 backdrop-blur-lg md:hidden">
          <div className="flex flex-col items-center justify-center h-full space-y-8">
            <a href="#" className="text-2xl text-zinc-300 hover:text-white transition-colors">Gallery</a>
            <a href="#" className="text-2xl text-zinc-300 hover:text-white transition-colors">Collections</a>
            <a href="#" className="text-2xl text-zinc-300 hover:text-white transition-colors">Premium</a>
            <a href="#" className="text-2xl text-zinc-300 hover:text-white transition-colors">About</a>
            <button
              onClick={() => setIsMenuOpen(false)}
              className="mt-8 px-6 py-3 bg-zinc-800/80 text-white rounded-lg hover:bg-zinc-700/80 transition-colors backdrop-blur-sm"
            >
              Close
            </button>
          </div>
        </div>
      )}

      {/* Hero Section with Carousel */}
      <section className="relative h-screen overflow-hidden">
        <div className="absolute inset-0">
          <div className="relative h-full w-full">
            {slides.map((slide, index) => (
              <div
                key={slide.id}
                className={`absolute inset-0 transition-opacity duration-1000 ${
                  index === currentSlide ? 'opacity-100' : 'opacity-0'
                }`}
              >
                <div className="h-full w-full bg-gradient-to-br from-zinc-800 via-zinc-900 to-black" />
              </div>
            ))}
          </div>
          <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent" />
        </div>
        
        <div className="relative z-10 flex h-full items-center justify-center">
          <div className="container mx-auto px-4 text-center">
            <div className="mb-8">
              {slides[currentSlide].premium && (
                <span className="inline-flex items-center rounded-full border border-amber-500/50 bg-amber-500/20 px-3 py-1 text-sm font-medium text-amber-400 backdrop-blur-sm mb-4">
                  <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                  </svg>
                  Premium
                </span>
              )}
            </div>
            <h2 className="mb-4 text-5xl font-bold text-white md:text-7xl">
              {slides[currentSlide].title}
            </h2>
            <p className="mb-8 text-xl text-zinc-300 md:text-2xl">
              {slides[currentSlide].category} Collection
            </p>
            <div className="flex justify-center space-x-4">
              <button
                onClick={handleShowToast}
                className="px-8 py-3 bg-white/90 text-zinc-900 rounded-lg hover:bg-white/80 transition-colors backdrop-blur-sm font-semibold"
              >
                Explore Gallery
              </button>
              <button className="px-8 py-3 bg-zinc-800/80 text-white rounded-lg hover:bg-zinc-700/80 transition-colors backdrop-blur-sm font-semibold border border-zinc-700">
                Learn More
              </button>
            </div>
          </div>
        </div>
        
        {/* Carousel Controls */}
        <div className="absolute bottom-8 left-1/2 -translate-x-1/2 z-20 flex space-x-4">
          <button
            onClick={prevSlide}
            className="p-3 bg-white/20 text-white rounded-full hover:bg-white/30 transition-colors backdrop-blur-sm"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <div className="flex items-center space-x-2">
            {slides.map((_, index) => (
              <button
                key={index}
                onClick={() => setCurrentSlide(index)}
                className={`h-2 rounded-full transition-all ${
                  index === currentSlide ? 'w-8 bg-white' : 'w-2 bg-white/50'
                }`}
              />
            ))}
          </div>
          <button
            onClick={nextSlide}
            className="p-3 bg-white/20 text-white rounded-full hover:bg-white/30 transition-colors backdrop-blur-sm"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </section>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-24">
        {/* Stats Section */}
        <section className="mb-24">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {[
              { label: 'Active Users', value: '10.5K', change: '+12%' },
              { label: 'Premium Members', value: '2.3K', change: '+8%' },
              { label: 'Total Wallpapers', value: '50K', change: '+25%' },
              { label: 'Downloads', value: '1.2M', change: '+18%' },
            ].map((stat, index) => (
              <div
                key={index}
                className="rounded-xl border border-zinc-800 bg-zinc-900/50 p-6 backdrop-blur-sm hover:bg-zinc-900/70 transition-colors"
              >
                <p className="text-sm text-zinc-400 mb-2">{stat.label}</p>
                <p className="text-3xl font-bold text-white mb-1">{stat.value}</p>
                <p className="text-sm text-green-400">{stat.change} this month</p>
              </div>
            ))}
          </div>
        </section>

        {/* Gallery Grid */}
        <section className="mb-24">
          <h3 className="text-3xl font-bold text-white mb-8">Featured Collections</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {[1, 2, 3, 4, 5, 6, 7, 8].map((item) => (
              <div key={item} className="group relative aspect-[16/9] overflow-hidden rounded-xl">
                <div className="absolute inset-0 bg-gradient-to-br from-zinc-700 via-zinc-800 to-zinc-900" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
                <div className="absolute bottom-0 left-0 right-0 p-4 translate-y-full group-hover:translate-y-0 transition-transform duration-300">
                  <p className="text-sm text-zinc-300 mb-1">Category</p>
                  <h4 className="text-lg font-semibold text-white">Wallpaper {item}</h4>
                </div>
                {item % 3 === 0 && (
                  <div className="absolute top-2 right-2">
                    <span className="inline-flex items-center rounded-full bg-amber-500/20 px-2 py-1 text-xs font-medium text-amber-400 backdrop-blur-sm">
                      Premium
                    </span>
                  </div>
                )}
              </div>
            ))}
          </div>
        </section>

        {/* Form Section */}
        <section className="mb-24">
          <div className="max-w-2xl mx-auto">
            <div className="rounded-xl border border-zinc-800 bg-zinc-900/50 p-8 backdrop-blur-sm">
              <h3 className="text-2xl font-bold text-white mb-6">Get Updates</h3>
              <form className="space-y-4" onSubmit={(e) => {
                e.preventDefault()
                handleShowToast()
                setFormData({ email: '', message: '' })
              }}>
                <div>
                  <label className="block text-sm font-medium text-zinc-300 mb-2" htmlFor="email">Email</label>
                  <input
                    id="email"
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                    className="w-full px-4 py-2 rounded-lg border border-zinc-700 bg-zinc-900/90 text-zinc-300 placeholder-zinc-500 focus:ring-2 focus:ring-zinc-500 focus:border-transparent backdrop-blur-sm transition-colors"
                    placeholder="you@example.com"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-zinc-300 mb-2" htmlFor="message">Message</label>
                  <textarea
                    id="message"
                    value={formData.message}
                    onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                    required
                    rows={4}
                    className="w-full px-4 py-2 rounded-lg border border-zinc-700 bg-zinc-900/90 text-zinc-300 placeholder-zinc-500 focus:ring-2 focus:ring-zinc-500 focus:border-transparent backdrop-blur-sm transition-colors"
                    placeholder="Your message..."
                  />
                </div>
                <button
                  type="submit"
                  disabled={!formData.email || !formData.message}
                  className="px-6 py-3 bg-blue-600/90 text-white rounded-lg hover:bg-blue-700/90 disabled:opacity-50 disabled:cursor-not-allowed transition-colors backdrop-blur-sm font-semibold"
                >
                  Send
                </button>
              </form>
            </div>
          </div>
        </section>
      </main>

      {/* Glass Toast Notification */}
      {showToast && (
        <div className="fixed bottom-8 right-8 max-w-sm rounded-lg border border-green-400/50 bg-green-500/90 shadow-lg backdrop-blur-sm">
          <div className="h-1 bg-black/10">
            <div className="h-full bg-white/30 animate-progress" style={{ animationDuration: '5s' }} />
          </div>
          <div className="p-4 text-white font-semibold">
            Message sent successfully!
          </div>
        </div>
      )}

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
          <div className="relative w-full max-w-md rounded-lg bg-zinc-900/90 p-6 shadow-lg">
            <button
              onClick={() => setIsModalOpen(false)}
              className="absolute top-3 right-3 rounded-md text-zinc-400 hover:text-white transition-colors"
              aria-label="Close modal"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
            <h3 className="mb-4 text-xl font-semibold text-white">Sign In</h3>
            <form className="space-y-4">
              <div>
                <label htmlFor="email-modal" className="block text-sm font-medium text-zinc-300 mb-1">Email</label>
                <input
                  id="email-modal"
                  type="email"
                  className="w-full px-4 py-2 rounded-lg border border-zinc-700 bg-zinc-900/90 text-zinc-300 placeholder-zinc-500 focus:ring-2 focus:ring-zinc-500 focus:border-transparent backdrop-blur-sm transition-colors"
                  placeholder="you@example.com"
                />
              </div>
              <div>
                <label htmlFor="password-modal" className="block text-sm font-medium text-zinc-300 mb-1">Password</label>
                <input
                  id="password-modal"
                  type="password"
                  className="w-full px-4 py-2 rounded-lg border border-zinc-700 bg-zinc-900/90 text-zinc-300 placeholder-zinc-500 focus:ring-2 focus:ring-zinc-500 focus:border-transparent backdrop-blur-sm transition-colors"
                  placeholder="••••••••"
                />
              </div>
              <button
                type="submit"
                className="w-full px-4 py-2 bg-blue-600/90 text-white rounded-lg hover:bg-blue-700/90 transition-colors backdrop-blur-sm font-semibold"
              >
                Sign In
              </button>
            </form>
          </div>
        </div>
      )}

      {/* Footer */}
      <footer className="border-t border-zinc-800 bg-zinc-900/80 backdrop-blur-sm">
        <div className="container mx-auto px-4 py-8 text-center">
          <p className="text-sm text-zinc-400">&copy; 2024 Frames Design System. All rights reserved.</p>
        </div>
      </footer>

      {/* Animation for toast progress bar */}
      <style>{`
        @keyframes progress {
          from { width: 100%; }
          to { width: 0%; }
        }

        .animate-progress {
          animation: progress linear forwards;
          background: rgba(255, 255, 255, 0.3);
        }
      `}</style>
    </div>
  )
}
