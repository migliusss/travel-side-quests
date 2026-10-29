import { NavLink, Route, Routes } from 'react-router-dom'
import HomePage from './pages/HomePage'
import BrowsePage from './pages/BrowsePage'
import SideQuestPage from './pages/SideQuestPage'
import SubmitPage from './pages/SubmitPage'
import AdminPage from './pages/AdminPage'

function NavItem({ to, children }: { to: string; children: React.ReactNode }) {
  return (
    <NavLink
      to={to}
      end={to === '/'}
      className={({ isActive }) =>
        `rounded-full px-3 py-1.5 text-sm font-semibold transition ${
          isActive ? 'bg-forest-800 text-paper' : 'text-forest-700 hover:text-forest-900'
        }`
      }
    >
      {children}
    </NavLink>
  )
}

/** Pages lay out their own width so the home page can run its hero full-bleed. */
function Contained({ children }: { children: React.ReactNode }) {
  return <div className="mx-auto max-w-6xl px-5 py-8">{children}</div>
}

export default function App() {
  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-20 border-b border-sage-200 bg-paper/90 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center gap-6 px-5 py-4">
          <NavLink to="/" className="flex items-baseline gap-2">
            <span className="font-display text-xl font-bold text-forest-900">Travel Side Quests</span>
            <span className="hidden text-xs text-forest-600 sm:inline">
              places that never make the top ten
            </span>
          </NavLink>

          <nav className="ml-auto flex items-center gap-1">
            <NavItem to="/browse">Browse</NavItem>
            <NavItem to="/submit">Add a place</NavItem>
            <NavItem to="/admin">Review</NavItem>
          </nav>
        </div>
      </header>

      <main className="flex-1">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route
            path="/browse"
            element={
              <Contained>
                <BrowsePage />
              </Contained>
            }
          />
          <Route
            path="/side-quests/:id"
            element={
              <Contained>
                <SideQuestPage />
              </Contained>
            }
          />
          <Route
            path="/submit"
            element={
              <Contained>
                <SubmitPage />
              </Contained>
            }
          />
          <Route
            path="/admin"
            element={
              <Contained>
                <AdminPage />
              </Contained>
            }
          />
          <Route
            path="*"
            element={
              <Contained>
                <p className="text-forest-600">Nothing here.</p>
              </Contained>
            }
          />
        </Routes>
      </main>

      <footer className="border-t border-sage-200 py-8 text-center text-xs text-forest-600">
        Every place here was recommended by someone, and says by whom.
      </footer>
    </div>
  )
}
