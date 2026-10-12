
'use strict';

// ==========================================
// ANIME FESTIVAL CINEMA
// Aplicación web - JavaScript frontend
// ==========================================

// Estado de la aplicación
let movies = [];
let shows = [];
let selectedShow = null;
let selectedSeat = null;
let token = localStorage.getItem('animefest_token') || '';
let authMode = 'login';

// Referencia rápida a elementos HTML
const $ = id => document.getElementById(id);

// Rutas de los videos
const INTRO_VIDEO = '/assets/video/entrada.mp4';
const OUTRO_VIDEO = '/assets/video/salida.mp4';

// ==========================================
// UTILIDADES
// ==========================================

function toast(message) {
    const element = $('toast');

    if (!element) return;

    element.textContent = message;
    element.style.display = 'block';

    setTimeout(() => {
        element.style.display = 'none';
    }, 3500);
}

function scrollToId(id) {
    const element = $(id);

    if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
    }
}

function esc(value) {
    return String(value ?? '').replace(/[&<>'"]/g, character => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        "'": '&#39;',
        '"': '&quot;'
    })[character]);
}

// ==========================================
// VIDEO DE ENTRADA Y SALIDA
// ==========================================

function playVideo(id, src) {
    const video = $(id);

    if (!video) {
        return null;
    }

    video.src = src;
    video.volume = 1;
    video.muted = false;
    video.currentTime = 0;

    return video;
}

// Inicia la introducción al cargar la página.
function showIntroVideo() {
    const video = $('introVideo');
    const intro = $('intro');

    if (!video || !intro) return;

    intro.classList.add('open');

    video.src = INTRO_VIDEO;
    video.volume = 1;
    video.muted = false;
    video.playsInline = true;

    video.play().catch(error => {
        // Chrome y Edge pueden bloquear el autoplay con sonido.
        // El usuario podrá activar el audio con un clic.
        console.warn(
            'El navegador requiere una interacción para reproducir el video con audio.',
            error
        );
    });
}

// Activa el audio mediante una interacción del usuario.
// Agregá el botón correspondiente al HTML.
async function activateIntroAudio() {
    const video = $('introVideo');

    if (!video) return;

    video.muted = false;
    video.volume = 1;

    try {
        await video.play();
        toast('Audio activado.');
    } catch (error) {
        console.error('Error al reproducir el video:', error);
        toast('No se pudo reproducir el video. Revisá el archivo.');
    }
}

// Entra al sitio y detiene la introducción.
function enterSite() {
    const video = $('introVideo');

    if (video) {
        video.pause();
    }

    $('intro')?.classList.remove('open');
    document.body.classList.remove('cinema');
}

// Omite la introducción.
function skipIntro() {
    enterSite();
}

// Reproduce el video de despedida.
function exitSite() {
    const outro = $('outro');

    if (!outro) return;

    outro.classList.add('open');
    document.body.classList.add('cinema');

    const video = playVideo('outroVideo', OUTRO_VIDEO);

    if (!video) return;

    video.onended = closeOutro;

    video.play().catch(error => {
        console.error('No se pudo reproducir el video de salida:', error);
        toast('No se pudo reproducir el video de salida.');
    });
}

// Detiene el video de despedida.
function stopOutro() {
    const video = $('outroVideo');

    if (video) {
        video.pause();
        video.onended = null;
    }

    $('outro')?.classList.remove('open');
    document.body.classList.remove('cinema');
}

// Cierra la experiencia de despedida.
function closeOutro() {
    stopOutro();

    // Los navegadores pueden impedir cerrar pestañas
    // que no fueron abiertas mediante JavaScript.
    try {
        window.close();
    } catch (error) {
        console.warn('El navegador no permitió cerrar la pestaña.');
    }
}

// ==========================================
// API Y COMUNICACIÓN CON JAVA
// ==========================================

async function api(url, options = {}) {
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
        ...(token ? { Authorization: token } : {}),
        ...(options.headers || {})
    };

    const response = await fetch(url, {
        ...options,
        headers
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.error || 'Ocurrió un error en la solicitud.');
    }

    return data;
}

// ==========================================
// CARGA INICIAL
// ==========================================

async function load() {
    try {
        movies = await api('/api/movies');
        shows = await api('/api/showtimes');

        renderMovies();
        renderShows();
        loadPublicVoting();
        loadAdmin();
        fillFilter();

        await Promise.all([
            loadFestival(),
            loadRecommendations(),
            loadStats()
        ]);

        updateUser();
        loadHistory();
    } catch (error) {
        console.error('Error al cargar la aplicación:', error);
        toast(error.message);
    }
}

// ==========================================
// TRAILER DE CADA PELÍCULA
// ==========================================
function openTrailer(movie) {
    const modal = $('trailer');
    const frame = $('trailerFrame');
    const fallback = $('trailerFallback');
    const title = $('trailerTitle');
    if (!modal || !frame || !fallback) return;

    title.textContent = movie.title || 'Trailer';
    frame.style.display = 'none';
    fallback.style.display = 'none';
    frame.src = '';

    const url = movie.trailer || '';
    if (url.includes('youtube.com/embed/')) {
        frame.src = url + (url.includes('?') ? '&' : '?') + 'autoplay=1&rel=0';
        frame.style.display = 'block';
    } else if (url) {
        fallback.innerHTML = `<p class="muted">El trailer oficial se abre en el sitio correspondiente.</p><a class="btn" href="${esc(url)}" target="_blank" rel="noopener">▶ Abrir trailer de ${esc(movie.title)}</a>`;
        fallback.style.display = 'grid';
    }
    modal.classList.add('open');
}

function closeTrailer() {
    const modal = $('trailer');
    const frame = $('trailerFrame');
    if (frame) frame.src = '';
    modal?.classList.remove('open');
}

// ==========================================
// CARTELERA DE PELÍCULAS
// ==========================================

function renderMovies() {
    const container = $('movies');
    const search = $('search');

    if (!container) return;

    const query = (search?.value || '').toLowerCase();

    const filteredMovies = movies.filter(movie => {
        const text = [
            movie.title,
            movie.genre,
            movie.description
        ].join(' ').toLowerCase();

        return text.includes(query);
    });

    container.innerHTML = filteredMovies.map(movie => `
        <article class="card movie-card">
            <img
                class="poster"
                src="${esc(movie.image)}"
                alt="Póster de ${esc(movie.title)}"
            >

            <div class="card-body">
                <button class="btn secondary trailer-btn" onclick='openTrailer(${JSON.stringify(movie)})'>▶ Ver trailer</button>
                <div class="rating">
                    ★ Jurado ${Number(movie.rating || 0).toFixed(1)}
                    · Público ${Number(movie.publicRating || 0).toFixed(1)}
                </div>

                <h3>${esc(movie.title)}</h3>

                <div class="meta">
                    ${esc(movie.genre)} · ${esc(movie.duration)} min
                </div>

                <p>${esc(movie.description)}</p>

                <div class="credits">
                    <b>Director:</b> ${esc(movie.director)}
                    <br>
                    <b>Elenco:</b>
                    ${esc(
                        Array.isArray(movie.actors)
                            ? movie.actors.join(', ') || 'Consultar ficha'
                            : 'Consultar ficha'
                    )}
                </div>
            </div>
        </article>
    `).join('') || '<p class="muted">No hay resultados.</p>';
}

// ==========================================
// FUNCIONES Y FILTROS
// ==========================================

function fillFilter() {
    const select = $('movieFilter');

    if (!select) return;

    select.innerHTML =
        '<option value="">Todas las películas</option>';

    const movieNames = [
        ...new Set(shows.map(show => show.movie))
    ];

    movieNames.forEach(movieName => {
        const option = document.createElement('option');

        option.value = movieName;
        option.textContent = movieName;

        select.appendChild(option);
    });
}

function renderShows() {
    const container = $('shows');
    const filter = $('movieFilter');

    if (!container) return;

    const selectedMovie = filter?.value || '';

    const filteredShows = shows.filter(show =>
        !selectedMovie || show.movie === selectedMovie
    );

    container.innerHTML = filteredShows.map(show => `
        <article class="show">
            <div class="show-top">
                <strong>${esc(show.movie)}</strong>

                <span class="type ${esc(show.type)}">
                    ${show.type === 'streaming'
                        ? 'STREAMING'
                        : 'PRESENCIAL'}
                </span>
            </div>

            <div class="pill">${esc(show.date)}</div>
            <div class="pill">${esc(show.time)}</div>

            <div class="pill">
                ${esc(show.city)}, ${esc(show.country)}
            </div>

            <div class="pill">${esc(show.room)}</div>

            <div class="occupancy">
                <div class="bar">
                    <span style="width:${Math.min(
                        100,
                        Math.max(0, Number(show.occupancy) || 0)
                    )}%"></span>
                </div>

                <small>
                    ${esc(show.free)} disponibles ·
                    ${Number(show.occupancy || 0).toFixed(1)}% ocupación
                </small>
            </div>

            <button
                class="btn"
                onclick="openReserve(${Number(show.id)})"
            >
                ${show.type === 'streaming'
                    ? 'Comprar acceso streaming'
                    : 'Reservar butaca'}
            </button>
        </article>
    `).join('') || '<p class="muted">No hay funciones para este filtro.</p>';
}

// ==========================================
// RESERVAS Y SELECCIÓN DE BUTACAS
// ==========================================

function openReserve(id) {
    if (!token) {
        openAuth('login');
        toast('Iniciá sesión para reservar.');
        return;
    }

    selectedShow = shows.find(show => show.id === id);

    if (!selectedShow) {
        toast('No se encontró la función seleccionada.');
        return;
    }

    selectedSeat = null;

    $('reserveInfo').innerHTML = `
        <b>${esc(selectedShow.movie)}</b> ·
        ${esc(selectedShow.date)} ·
        ${esc(selectedShow.time)}
        <br>
        ${esc(selectedShow.city)}, ${esc(selectedShow.country)} ·
        ${esc(selectedShow.room)}
    `;

    $('reserve').classList.add('open');

    const tariff = $('reserveTariff');
    if (tariff) tariff.value = 'GENERAL';
    updateReservePrice();

    const isStreaming = selectedShow.type === 'streaming';

    $('seatArea').style.display = isStreaming ? 'none' : 'block';
    $('streamArea').style.display = isStreaming ? 'block' : 'none';

    if (!isStreaming) {
        const occupiedSeats = selectedShow.occupied || [];
        const capacity = Math.max(
            0,
            Math.min(1000, Number(selectedShow.capacity) || 0)
        );

        $('seatMap').innerHTML = Array.from(
            { length: capacity },
            (_, index) => {
                const seatNumber = index + 1;

                const occupied = occupiedSeats.some(
                    seat => String(seat) === String(seatNumber)
                );

                return `
                    <button
                        class="seat ${occupied ? 'occupied' : ''}"
                        ${occupied ? 'disabled' : ''}
                        onclick="selectSeat(${seatNumber}, this)"
                    >
                        ${seatNumber}
                    </button>
                `;
            }
        ).join('');
    }
}

function selectSeat(number, element) {
    if (!selectedShow || selectedShow.type === 'streaming') {
        return;
    }

    if (!element || element.disabled) {
        return;
    }

    selectedSeat = number;

    document.querySelectorAll('.seat.selected').forEach(seat => {
        seat.classList.remove('selected');
    });

    element.classList.add('selected');
}

function updateReservePrice() {
    if (!selectedShow) return;
    const tariff = $('reserveTariff')?.value || 'GENERAL';
    const discounts = {GENERAL: 0, ESTUDIANTE: 30, JUBILADO: 40};
    const base = selectedShow.type === 'streaming' ? 5000 : 8000;
    const discount = discounts[tariff] || 0;
    const days = Math.floor((new Date(selectedShow.date + 'T00:00:00') - new Date()) / 86400000);
    const early = days >= 7 ? 10 : 0;
    const price = base * (1 - discount / 100) * (1 - early / 100);
    const el = $('reservePrice');
    if (el) el.textContent = `Precio estimado: $ ${price.toFixed(2)}${early ? ' · incluye 10% por compra anticipada' : ''}`;
}

function closeReserve() {
    $('reserve')?.classList.remove('open');

    selectedShow = null;
    selectedSeat = null;
}

async function confirmReservation() {
    if (!selectedShow) {
        toast('Seleccioná una función.');
        return;
    }

    if (selectedShow.type !== 'streaming' && selectedSeat === null) {
        toast('Seleccioná una butaca.');
        return;
    }

    try {
        const body = {
            showtimeId: selectedShow.id,
            tariff: $('reserveTariff')?.value || 'GENERAL'
        };

        if (selectedSeat !== null) {
            body.seat = selectedSeat;
        }

        const data = await api('/api/reserve', {
            method: 'POST',
            body: new URLSearchParams(body)
        });

        toast(
            `Entrada ${data.type} confirmada · Código: ${data.code}`
        );

        closeReserve();

        shows = await api('/api/showtimes');

        renderShows();
        fillFilter();
        loadHistory();
        loadStats();
    } catch (error) {
        console.error('Error al reservar:', error);
        toast(error.message);
    }
}

// ==========================================
// AUTENTICACIÓN Y CUENTAS
// ==========================================

function openAuth(mode) {
    authMode = mode;

    $('auth').classList.add('open');

    $('authTitle').textContent =
        mode === 'login' ? 'Iniciar sesión' : 'Crear cuenta';

    $('authSubmit').textContent =
        mode === 'login' ? 'Ingresar' : 'Crear cuenta';

    $('name').style.display =
        mode === 'login' ? 'none' : 'block';

    $('country').style.display =
        mode === 'login' ? 'none' : 'block';

    $('name').required = mode === 'register';
    $('country').required = mode === 'register';

    $('authSwitch').textContent =
        mode === 'login'
            ? 'Ingresá para reservar y consultar tu historial.'
            : 'Registrate para comprar entradas presenciales o streaming.';
}

function closeAuth() {
    $('auth')?.classList.remove('open');
}

async function submitAuth(event) {
    event.preventDefault();

    try {
        const body = {
            email: $('email').value,
            password: $('password').value
        };

        if (authMode === 'register') {
            body.name = $('name').value;
            body.country = $('country').value;
        }

        const data = await api(
            authMode === 'login' ? '/api/login' : '/api/register',
            {
                method: 'POST',
                body: new URLSearchParams(body)
            }
        );

        token = data.token;

        localStorage.setItem('animefest_token', token);

        closeAuth();
        updateUser();
        loadHistory();

        toast(`Bienvenido/a, ${data.name}`);
    } catch (error) {
        console.error('Error de autenticación:', error);
        toast(error.message);
    }
}

async function logout() {
    try {
        if (token) {
            await api('/api/logout', {
                method: 'POST'
            });
        }
    } catch (error) {
        console.error('Error al cerrar sesión en el servidor:', error);
    } finally {
        token = '';

        localStorage.removeItem('animefest_token');

        updateUser();

        $('history').innerHTML =
            '<div class="empty">Iniciá sesión para consultar tus entradas.</div>';

        toast('Sesión cerrada.');
    }
}

function updateUser() {
    const loggedIn = Boolean(token);

    $('loginBtn').style.display =
        loggedIn ? 'none' : 'inline-block';

    $('registerBtn').style.display =
        loggedIn ? 'none' : 'inline-block';

    $('logoutBtn').style.display =
        loggedIn ? 'inline-block' : 'none';

    $('userLabel').textContent =
        loggedIn ? 'Cuenta activa' : '';
}

// ==========================================
// RECOMENDACIONES DE ANIME
// ==========================================

async function loadRecommendations() {
    try {
        const recommendations = await api('/api/recommendations');

        $('recommendations').innerHTML = recommendations.map(movie => `
            <article class="card">
                <img
                    class="poster"
                    src="${esc(movie.image)}"
                    alt="${esc(movie.title)}"
                >

                <div class="card-body">
                    <h3>${esc(movie.title)}</h3>

                    <div class="rating">
                        ★ ${Number(movie.rating || 0).toFixed(1)}
                        · recomendada
                    </div>

                    <p>${esc(movie.description)}</p>
                </div>
            </article>
        `).join('') || '<p class="muted">No hay recomendaciones disponibles.</p>';
    } catch (error) {
        console.error('Error al cargar recomendaciones:', error);
    }
}

// ==========================================
// FESTIVAL, EDICIONES Y PREMIOS
// ==========================================

async function loadFestival() {
    try {
        const data = await api('/api/festival');

        $('festivalData').innerHTML = (data.editions || []).map(edition => `
            <article class="festival-card">
                <h3>Edición ${esc(edition.year)}</h3>

                <div class="muted">
                    ${esc(edition.city)} ·
                    ${esc(edition.start)} →
                    ${esc(edition.end)}
                </div>

                <div class="section-cards">
                    ${(edition.sections || []).map(section => `
                        <div class="section-card">
                            <b>${esc(section.type)}</b>

                            <span>${esc(section.award)}</span>

                            <small>
                                ${esc(section.awardDescription)}
                            </small>

                            <em>
                                Ganadora jurado:
                                ${esc(section.winner || 'A definir')}
                                <br>
                                Premio del público:
                                ${esc(section.publicWinner || 'A definir')}
                            </em>
                        </div>
                    `).join('')}
                </div>
            </article>
        `).join('') || '<p class="muted">No hay ediciones disponibles.</p>';
    } catch (error) {
        console.error('Error al cargar el festival:', error);
    }
}

// ==========================================
// HISTORIAL DE ENTRADAS
// ==========================================

async function loadHistory() {
    if (!token) {
        $('history').innerHTML =
            '<div class="empty">Iniciá sesión para consultar tus entradas.</div>';

        return;
    }

    try {
        const history = await api('/api/history');

        $('history').innerHTML = history.length
            ? history.map(entry => `
                <div class="history-row">
                    <b>${esc(entry.movie)}</b>

                    <span>
                        ${esc(entry.date)} · ${esc(entry.time)}
                    </span>

                    <span>
                        ${esc(entry.type)} · ${esc(entry.detail)}
                    </span>

                    <code>${esc(entry.code)}</code>
                </div>
            `).join('')
            : '<div class="empty">Todavía no tenés entradas.</div>';
    } catch (error) {
        console.error('Error al cargar el historial:', error);

        $('history').innerHTML =
            '<div class="empty">No se pudo cargar el historial.</div>';
    }
}

// ==========================================
// ESTADÍSTICAS DEL FESTIVAL
// ==========================================

async function loadStats() {
    try {
        const stats = await api('/api/stats');

        $('stats').innerHTML = `
            <div>
                <b>${esc(stats.movies)}</b>
                <span>películas anime</span>
            </div>

            <div>
                <b>${esc(stats.showtimes)}</b>
                <span>funciones</span>
            </div>

            <div>
                <b>${esc(stats.presential)}</b>
                <span>presenciales</span>
            </div>

            <div>
                <b>${esc(stats.streaming)}</b>
                <span>streaming</span>
            </div>

            <div>
                <b>${esc(stats.sold)}</b>
                <span>entradas vendidas</span>
            </div>

            <div>
                <b>${Number(stats.occupancy || 0).toFixed(1)}%</b>
                <span>ocupación presencial</span>
            </div>
        `;
    } catch (error) {
        console.error('Error al cargar las estadísticas:', error);
    }
}


document.addEventListener('change', event => {
    if (event.target?.id === 'reserveTariff') updateReservePrice();
});


// ==========================================
// PREMIO DEL PÚBLICO
// ==========================================

async function loadPublicVoting() {
    const container = $('publicVoting');
    if (!container) return;

    try {
        const data = await api('/api/movies');
        container.innerHTML = data.map(movie => `
            <article class="card">
                <img class="poster" src="${esc(movie.image)}" alt="${esc(movie.title)}">
                <div class="card-body">
                    <h3>${esc(movie.title)}</h3>
                    <div class="rating">
                        ★ Jurado ${Number(movie.rating || 0).toFixed(1)}
                        · Público ${Number(movie.publicRating || 0).toFixed(1)}
                    </div>
                    <div class="vote-row">
                        <select class="select" id="vote-${encodeURIComponent(movie.title)}">
                            <option value="1">★ 1</option>
                            <option value="2">★ 2</option>
                            <option value="3">★ 3</option>
                            <option value="4">★ 4</option>
                            <option value="5">★ 5</option>
                        </select>
                        <button class="btn" onclick="voteMovie(${JSON.stringify(movie.title)})">Votar</button>
                    </div>
                </div>
            </article>
        `).join('') || '<p class="muted">No hay películas disponibles.</p>';
    } catch (error) {
        console.error('Error al cargar votaciones:', error);
    }
}

async function voteMovie(title) {
    if (!token) {
        openAuth('login');
        toast('Iniciá sesión para votar.');
        return;
    }

    const select = $('vote-' + encodeURIComponent(title));
    try {
        const data = await api('/api/vote', {
            method: 'POST',
            body: new URLSearchParams({
                movie: title,
                score: select ? select.value : '5'
            })
        });
        toast(data.message || 'Voto registrado.');
        await loadPublicVoting();
        await loadFestival();
        await loadAdmin();
    } catch (error) {
        toast(error.message);
    }
}

// ==========================================
// PANEL WEB DE GESTIÓN
// ==========================================

async function adminSubmit(event, url) {
    event.preventDefault();
    try {
        const form = event.currentTarget;
        const data = await api(url, {
            method: 'POST',
            body: new URLSearchParams(new FormData(form))
        });
        toast(data.message || 'Operación realizada.');
        form.reset();
        await refreshAll();
    } catch (error) {
        toast(error.message);
    }
}

async function buyPass(event) {
    event.preventDefault();
    try {
        const form = event.currentTarget;
        const data = await api('/api/admin/pass', {
            method: 'POST',
            body: new URLSearchParams(new FormData(form))
        });
        toast(`${data.message} Código: ${data.code} · Precio: $ ${Number(data.price || 0).toFixed(2)}`);
        form.reset();
        await refreshAll();
    } catch (error) {
        toast(error.message);
    }
}

async function loadAdmin() {
    try {
        const data = await api('/api/admin/data');

        const row = (values) => `<tr>${values.map(v => `<td>${esc(v)}</td>`).join('')}</tr>`;
        const set = (id, html) => { const el=$(id); if(el) el.innerHTML=html; };

        set('adminMovies', (data.movies || []).map(m => {
            const values = [m.title,m.director,m.genre,m.duration,m.rating?.toFixed ? m.rating.toFixed(1) : m.rating,m.publicRating?.toFixed ? m.publicRating.toFixed(1) : m.publicRating,m.votes];
            return `<tr>${values.map(v => `<td>${esc(v)}</td>`).join('')}<td><button class="btn secondary table-trailer" onclick='openTrailer(${JSON.stringify(m)})'>▶ Trailer</button></td></tr>`;
        }).join(''));

        const actorMovieMap = {};
        (data.movies || []).forEach(m => (m.actors || []).forEach(a => {
            actorMovieMap[a] = actorMovieMap[a] ? actorMovieMap[a] + ', ' + m.title : m.title;
        }));
        set('adminActors', (data.actors || []).map(a => row([a.name,a.country,actorMovieMap[a.name] || '—'])).join(''));
        const directorMovieMap = {};
        (data.movies || []).forEach(m => { directorMovieMap[m.director] = directorMovieMap[m.director] ? directorMovieMap[m.director] + ', ' + m.title : m.title; });
        set('adminDirectors', (data.directors || []).map(d => row([d.name,d.country,directorMovieMap[d.name] || '—'])).join(''));
        set('adminJuries', (data.juries || []).map(j => row([j.name,j.country,j.evaluations])).join(''));
        set('adminEvaluations', (data.evaluations || []).map(e => row([e.jury,e.movie,e.score])).join(''));
        set('adminVotes', (data.votes || []).map(v => row([v.spectator,v.movie,v.score])).join(''));

        let editionRows = [];
        (data.editions || []).forEach(e => (e.sections || []).forEach(sec => {
            editionRows.push(row([e.year,sec.type.replaceAll('_',' '),sec.award,sec.winner || 'A definir',sec.publicWinner || 'A definir',(sec.movies || []).join(', ') || '—']));
        }));
        set('adminEditions', editionRows.join(''));
        set('adminRooms', (data.rooms || []).map(r => row([r.name,r.capacity])).join(''));
        set('adminSpectators', (data.spectators || []).map(x => row([x.name,x.email,x.tickets,x.passes])).join(''));
        set('adminEntries', (data.entries || []).map(e => row([e.code,e.movie,e.spectator,e.type,e.detail,`$ ${Number(e.price||0).toFixed(2)}`])).join(''));
        set('adminPasses', (data.passes || []).map(a => row([a.code,a.type,a.spectator,a.tariff,`$ ${Number(a.price||0).toFixed(2)}`,a.reservations])).join(''));

        fillDatalist('movieOptions', (data.movies || []).map(x=>x.title));
        fillDatalist('directorOptions', (data.directors || []).map(x=>x.name));
        fillDatalist('actorOptions', (data.actors || []).map(x=>x.name));
        fillDatalist('juryOptions', (data.juries || []).map(x=>x.name));
        fillDatalist('roomOptions', (data.rooms || []).map(x=>x.name));
        fillDatalist('spectatorOptions', (data.spectators || []).map(x=>x.name));
        fillDatalist('passOptions', (data.passes || []).map(x=>x.code));
    } catch (error) {
        console.error('Error al cargar gestión:', error);
    }
}

function fillDatalist(id, values) {
    const el = $(id);
    if (!el) return;
    el.innerHTML = [...new Set(values)].map(v => `<option value="${esc(v)}"></option>`).join('');
}

function toggleShowForm(select) {
    const form = select.closest('form');
    if (!form) return;
    const streaming = select.value === 'streaming';
    form.querySelector('.show-room').style.display = streaming ? 'none' : 'block';
    form.querySelector('.show-capacity').style.display = streaming ? 'block' : 'none';
    form.querySelector('.show-link').style.display = streaming ? 'block' : 'none';
}

function togglePassForm(select) {
    const form = select.closest('form');
    if (!form) return;
    const days = form.querySelector('.pass-days');
    if (days) days.style.display = select.value === 'TRES_DIAS' ? 'block' : 'none';
}

async function refreshAll() {
    await load();
    await loadPublicVoting();
    await loadAdmin();
}

// Actualiza los datos administrativos cuando se abre Gestión.
document.addEventListener('DOMContentLoaded', () => {
    loadAdmin();
    loadPublicVoting();
});

// ==========================================
// INICIALIZACIÓN
// ==========================================

window.addEventListener('load', () => {
    showIntroVideo();
    load();
});
