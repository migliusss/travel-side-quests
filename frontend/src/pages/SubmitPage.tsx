import { useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ApiError, api } from '../api/client'
import type { CreateSideQuest, SourceSubmission } from '../api/types'

interface UploadedImage {
  storageKey: string
  previewUrl: string
  altText: string
}

const emptySource: SourceSubmission = { name: '', sourceTypeId: 0, url: '', author: '', pageNumber: '' }

export default function SubmitPage() {
  const [countryCode, setCountryCode] = useState('')
  const [destinationMode, setDestinationMode] = useState<'existing' | 'new'>('new')
  const [destinationId, setDestinationId] = useState<number | ''>('')
  const [destinationName, setDestinationName] = useState('')
  const [destinationType, setDestinationType] = useState('')

  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [address, setAddress] = useState('')
  const [latitude, setLatitude] = useState('')
  const [longitude, setLongitude] = useState('')
  const [websiteUrl, setWebsiteUrl] = useState('')
  const [submittedBy, setSubmittedBy] = useState('')

  const [categoryIds, setCategoryIds] = useState<number[]>([])
  const [images, setImages] = useState<UploadedImage[]>([])
  const [sources, setSources] = useState<SourceSubmission[]>([])
  const [uploadError, setUploadError] = useState<string | null>(null)

  const countries = useQuery({
    queryKey: ['countries', 'all'],
    queryFn: () => api.countries(false),
  })

  const selectedCountry = countries.data?.find((country) => country.countryCode === countryCode)

  const destinations = useQuery({
    queryKey: ['destinations', selectedCountry?.id],
    queryFn: () => api.destinations(selectedCountry?.id),
    enabled: Boolean(selectedCountry),
  })

  const categories = useQuery({ queryKey: ['categories'], queryFn: api.categories })
  const sourceTypes = useQuery({ queryKey: ['sourceTypes'], queryFn: api.sourceTypes })

  const upload = useMutation({
    mutationFn: api.uploadImage,
    onSuccess: (result, file) => {
      setUploadError(null)
      setImages((current) => [
        ...current,
        { storageKey: result.storageKey, previewUrl: URL.createObjectURL(file), altText: '' },
      ])
    },
    onError: (error: ApiError) => setUploadError(error.message),
  })

  const submit = useMutation({
    mutationFn: (payload: CreateSideQuest) => api.submit(payload),
  })

  const fieldErrors = submit.error instanceof ApiError ? submit.error.fieldErrors : {}

  function onSubmit(event: FormEvent) {
    event.preventDefault()

    submit.mutate({
      name,
      description,
      address: address || null,
      latitude: Number(latitude),
      longitude: Number(longitude),
      websiteUrl: websiteUrl || null,
      countryCode,
      destinationId: destinationMode === 'existing' && destinationId ? Number(destinationId) : null,
      destinationName: destinationMode === 'new' ? destinationName : null,
      destinationType: destinationMode === 'new' && destinationType ? destinationType : null,
      categoryIds,
      images: images.map((image) => ({
        storageKey: image.storageKey,
        altText: image.altText || null,
      })),
      sources: sources
        .filter((source) => source.name.trim() && source.sourceTypeId)
        .map((source) => ({
          name: source.name,
          sourceTypeId: Number(source.sourceTypeId),
          url: source.url || null,
          author: source.author || null,
          pageNumber: source.pageNumber || null,
        })),
      submittedBy: submittedBy || null,
    })
  }

  if (submit.isSuccess) {
    return (
      <div className="mx-auto max-w-xl rounded-2xl border border-pink-300 bg-pink-50 p-10 text-center">
        <h1 className="font-display text-2xl font-bold text-forest-900">Thank you</h1>
        <p className="mt-3 text-forest-700">
          <strong>{submit.data.name}</strong> is in the queue. Someone will read it before it
          goes live, so it will not show up on the site just yet.
        </p>
        <div className="mt-6 flex justify-center gap-3">
          <Link to="/browse" className="btn-ghost">
            Back to browsing
          </Link>
          <button type="button" className="btn-primary" onClick={() => submit.reset()}>
            Add another
          </button>
        </div>
      </div>
    )
  }

  return (
    <form onSubmit={onSubmit} className="mx-auto max-w-2xl space-y-8">
      <header>
        <h1 className="font-display text-2xl font-bold">Add a place</h1>
        <p className="mt-1 text-sm text-forest-600">
          Somewhere worth the detour that no list sends people to. Every submission is read
          before it appears.
        </p>
      </header>

      <Section title="Where is it">
        <Field label="Country" error={fieldErrors.countryCode}>
          <select
            className="field"
            value={countryCode}
            required
            onChange={(event) => {
              setCountryCode(event.target.value)
              setDestinationId('')
            }}
          >
            <option value="">Choose a country</option>
            {countries.data?.map((country) => (
              <option key={country.id} value={country.countryCode}>
                {country.name}
              </option>
            ))}
          </select>
        </Field>

        <div className="flex gap-2">
          <ModeButton
            active={destinationMode === 'new'}
            onClick={() => setDestinationMode('new')}
          >
            New region or town
          </ModeButton>
          <ModeButton
            active={destinationMode === 'existing'}
            onClick={() => setDestinationMode('existing')}
            disabled={!destinations.data?.length}
          >
            One already listed
          </ModeButton>
        </div>

        {destinationMode === 'existing' ? (
          <Field label="Destination" error={fieldErrors.destinationChoiceValid}>
            <select
              className="field"
              value={destinationId}
              onChange={(event) => setDestinationId(Number(event.target.value))}
            >
              <option value="">Choose a destination</option>
              {destinations.data?.map((destination) => (
                <option key={destination.id} value={destination.id}>
                  {destination.name}
                </option>
              ))}
            </select>
          </Field>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Region or town" error={fieldErrors.destinationName}>
              <input
                className="field"
                value={destinationName}
                onChange={(event) => setDestinationName(event.target.value)}
                placeholder="Westfjords"
              />
            </Field>
            <Field label="What kind (optional)">
              <input
                className="field"
                value={destinationType}
                onChange={(event) => setDestinationType(event.target.value)}
                placeholder="REGION, CITY, ISLAND…"
              />
            </Field>
          </div>
        )}

        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="Latitude" error={fieldErrors.latitude}>
            <input
              className="field"
              value={latitude}
              required
              inputMode="decimal"
              onChange={(event) => setLatitude(event.target.value)}
              placeholder="65.5941799"
            />
          </Field>
          <Field label="Longitude" error={fieldErrors.longitude}>
            <input
              className="field"
              value={longitude}
              required
              inputMode="decimal"
              onChange={(event) => setLongitude(event.target.value)}
              placeholder="-24.00924"
            />
          </Field>
        </div>

        <Field label="Address (optional)" error={fieldErrors.address}>
          <input
            className="field"
            value={address}
            onChange={(event) => setAddress(event.target.value)}
          />
        </Field>
      </Section>

      <Section title="What it is">
        <Field label="Name" error={fieldErrors.name}>
          <input
            className="field"
            value={name}
            required
            onChange={(event) => setName(event.target.value)}
            placeholder="Látrabjarg"
          />
        </Field>

        <Field label="Why go" error={fieldErrors.description}>
          <textarea
            className="field min-h-32"
            value={description}
            required
            onChange={(event) => setDescription(event.target.value)}
            placeholder="What makes it worth the detour."
          />
        </Field>

        <Field label="Website (optional)" error={fieldErrors.websiteUrl}>
          <input
            className="field"
            value={websiteUrl}
            onChange={(event) => setWebsiteUrl(event.target.value)}
            placeholder="https://…"
          />
        </Field>

        <Field label="Kind of place" error={fieldErrors.categoryIds}>
          <div className="flex flex-wrap gap-1.5">
            {categories.data?.map((category) => {
              const active = categoryIds.includes(category.id)

              return (
                <button
                  key={category.id}
                  type="button"
                  onClick={() =>
                    setCategoryIds((current) =>
                      active
                        ? current.filter((entry) => entry !== category.id)
                        : [...current, category.id],
                    )
                  }
                  className={`rounded-full px-2.5 py-1 text-xs font-medium transition ${
                    active
                      ? 'bg-lime-300 text-forest-900'
                      : 'bg-lime-50 text-forest-700 hover:bg-lime-100'
                  }`}
                >
                  {category.name.toLowerCase()}
                </button>
              )
            })}
          </div>
        </Field>
      </Section>

      <Section title="Photos">
        <input
          type="file"
          accept="image/jpeg,image/png"
          multiple
          className="field"
          onChange={(event) => {
            Array.from(event.target.files ?? []).forEach((file) => upload.mutate(file))
            event.target.value = ''
          }}
        />
        <p className="text-xs text-forest-600">
          JPEG or PNG, up to 5&nbsp;MB. Photos are re-encoded on upload, which removes the
          location data cameras embed in them.
        </p>

        {upload.isPending && <p className="text-sm text-forest-600">Uploading…</p>}
        {uploadError && <p className="text-sm text-forest-900">{uploadError}</p>}

        {images.length > 0 && (
          <ul className="grid gap-3 sm:grid-cols-2">
            {images.map((image, index) => (
              <li key={image.storageKey} className="space-y-2 rounded-xl border border-sage-200 p-2">
                <img
                  src={image.previewUrl}
                  alt=""
                  className="h-32 w-full rounded-lg object-cover"
                />
                <input
                  className="field"
                  placeholder="Describe the photo"
                  value={image.altText}
                  onChange={(event) =>
                    setImages((current) =>
                      current.map((entry, position) =>
                        position === index ? { ...entry, altText: event.target.value } : entry,
                      ),
                    )
                  }
                />
                <button
                  type="button"
                  className="text-xs font-semibold text-forest-900 hover:underline"
                  onClick={() =>
                    setImages((current) => current.filter((_, position) => position !== index))
                  }
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
        )}
      </Section>

      <Section title="Where you heard about it">
        <p className="-mt-2 text-xs text-forest-600">
          The part that makes this site useful. A book, a video, a friend — whatever sent you
          there.
        </p>

        {sources.map((source, index) => (
          <div key={index} className="grid gap-3 rounded-xl border border-sage-200 p-3 sm:grid-cols-2">
            <Field label="What it was">
              <input
                className="field"
                value={source.name}
                placeholder="Intrepid Iceland"
                onChange={(event) =>
                  setSources((current) =>
                    current.map((entry, position) =>
                      position === index ? { ...entry, name: event.target.value } : entry,
                    ),
                  )
                }
              />
            </Field>

            <Field label="Kind of source">
              <select
                className="field"
                value={source.sourceTypeId || ''}
                onChange={(event) =>
                  setSources((current) =>
                    current.map((entry, position) =>
                      position === index
                        ? { ...entry, sourceTypeId: Number(event.target.value) }
                        : entry,
                    ),
                  )
                }
              >
                <option value="">Choose one</option>
                {sourceTypes.data?.map((type) => (
                  <option key={type.id} value={type.id}>
                    {type.name.toLowerCase()}
                  </option>
                ))}
              </select>
            </Field>

            <Field label="Author (optional)">
              <input
                className="field"
                value={source.author ?? ''}
                onChange={(event) =>
                  setSources((current) =>
                    current.map((entry, position) =>
                      position === index ? { ...entry, author: event.target.value } : entry,
                    ),
                  )
                }
              />
            </Field>

            <Field label="Link or page (optional)">
              <input
                className="field"
                value={source.url ?? ''}
                placeholder="https://…"
                onChange={(event) =>
                  setSources((current) =>
                    current.map((entry, position) =>
                      position === index ? { ...entry, url: event.target.value } : entry,
                    ),
                  )
                }
              />
            </Field>

            <button
              type="button"
              className="justify-self-start text-xs font-semibold text-forest-900 hover:underline"
              onClick={() =>
                setSources((current) => current.filter((_, position) => position !== index))
              }
            >
              Remove this source
            </button>
          </div>
        ))}

        <button
          type="button"
          className="btn-ghost"
          onClick={() => setSources((current) => [...current, { ...emptySource }])}
        >
          Add a source
        </button>
      </Section>

      <Section title="Credit (optional)">
        <Field label="Who to credit" error={fieldErrors.submittedBy}>
          <input
            className="field"
            value={submittedBy}
            onChange={(event) => setSubmittedBy(event.target.value)}
            placeholder="A name or handle shown with the entry"
          />
        </Field>
      </Section>

      {submit.isError && (
        <div className="rounded-xl bg-danger-50 p-4 text-sm text-forest-900">
          {(submit.error as ApiError).message}
        </div>
      )}

      <button type="submit" className="btn-primary w-full" disabled={submit.isPending}>
        {submit.isPending ? 'Sending…' : 'Submit for review'}
      </button>
    </form>
  )
}

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <section className="space-y-4 rounded-2xl border border-sage-200 bg-white p-5">
      <h2 className="font-display text-lg font-semibold">{title}</h2>
      {children}
    </section>
  )
}

function Field({
  label,
  error,
  children,
}: {
  label: string
  error?: string
  children: React.ReactNode
}) {
  return (
    <div>
      <span className="label">{label}</span>
      {children}
      {error && <p className="mt-1 text-xs text-forest-900">{error}</p>}
    </div>
  )
}

function ModeButton({
  active,
  disabled,
  onClick,
  children,
}: {
  active: boolean
  disabled?: boolean
  onClick: () => void
  children: React.ReactNode
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className={`rounded-lg px-3 py-1.5 text-sm font-semibold transition disabled:opacity-40 ${
        active ? 'bg-forest-700 text-white' : 'bg-lime-50 text-forest-700 hover:bg-lime-100'
      }`}
    >
      {children}
    </button>
  )
}
