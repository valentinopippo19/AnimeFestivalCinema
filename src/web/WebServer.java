package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ia.Recomendador;
import ia.RecomendadorAnime;
import modelo.*;
import patrones.AnimeFestivalFacade;
import patrones.RecomendadorAdapter;
import patrones.RecomendadorExterno;
import excepciones.AbonoInvalidoException;
import patrones.EventoReserva;
import seguridad.Cuenta;
import seguridad.CuentaServicio;
import seguridad.SesionManager;
import servicios.EntradaServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Controller HTTP + Facade. La lógica de negocio permanece en servicios y dominio. */
public class WebServer {
    private final PeliculaServicio peliculaServicio;
    private final EntradaServicio entradaServicio;
    private final CuentaServicio cuentaServicio;
    private final FestivalServicio festivalServicio;
    private final AnimeFestivalFacade facade;
    private final SesionManager sesiones = SesionManager.getInstance();
    private final Recomendador recomendador = new RecomendadorAnime();
    private final Path webRoot;
    private HttpServer server;

    public WebServer(PeliculaServicio peliculaServicio, EntradaServicio entradaServicio, CuentaServicio cuentaServicio,
                     FestivalServicio festivalServicio, AnimeFestivalFacade facade, Path webRoot) {
        this.peliculaServicio = peliculaServicio; this.entradaServicio = entradaServicio; this.cuentaServicio = cuentaServicio;
        this.festivalServicio = festivalServicio; this.facade = facade; this.webRoot = webRoot;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/movies", this::movies);
        server.createContext("/api/showtimes", this::showtimes);
        server.createContext("/api/festival", this::festival);
        server.createContext("/api/people", this::people);
        server.createContext("/api/register", this::register);
        server.createContext("/api/login", this::login);
        server.createContext("/api/logout", this::logout);
        server.createContext("/api/reserve", this::reserve);
        server.createContext("/api/history", this::history);
        server.createContext("/api/recommendations", this::recommendations);
        server.createContext("/api/stats", this::stats);
        server.createContext("/api/stream", this::stream);

        // Gestión web equivalente a los paneles del proyecto original.
        server.createContext("/api/admin/data", this::adminData);
        server.createContext("/api/admin/director", this::adminDirector);
        server.createContext("/api/admin/actor", this::adminActor);
        server.createContext("/api/admin/movie", this::adminMovie);
        server.createContext("/api/admin/movie/delete", this::adminMovieDelete);
        server.createContext("/api/admin/movie/actor", this::adminMovieActor);
        server.createContext("/api/admin/jury", this::adminJury);
        server.createContext("/api/admin/evaluation", this::adminEvaluation);
        server.createContext("/api/admin/edition", this::adminEdition);
        server.createContext("/api/admin/section", this::adminSection);
        server.createContext("/api/admin/section/movie", this::adminSectionMovie);
        server.createContext("/api/admin/section/movie/remove", this::adminSectionMovieRemove);
        server.createContext("/api/admin/room", this::adminRoom);
        server.createContext("/api/admin/showtime", this::adminShowtime);
        server.createContext("/api/admin/spectator", this::adminSpectator);
        server.createContext("/api/admin/pass", this::adminPass);
        server.createContext("/api/admin/pass/reserve", this::adminPassReserve);
        server.createContext("/api/vote", this::votePublico);

        server.createContext("/", this::staticFiles);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool()); server.start();
    }
    public String url() { return "http://127.0.0.1:" + server.getAddress().getPort() + "/"; }

    private void movies(HttpExchange ex) throws IOException {
        List<String> out = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) if (p.isAnime()) {
            String actors = p.getActores().stream().map(Actor::getNombre).map(this::q).collect(java.util.stream.Collectors.joining(","));
            out.add("{\"title\":"+q(p.getTitulo())+",\"genre\":"+q(p.getGenero())+",\"duration\":"+p.getDuracion()+",\"rating\":"+num(p.calcularPromedio())+",\"publicRating\":"+num(p.calcularPromedioPublico())+",\"votes\":"+p.getVotosPublico().size()+",\"image\":"+q(p.getImagenUrl())+",\"description\":"+q(p.getDescripcion())+",\"director\":"+q(p.getDirector().getNombre())+",\"actors\":["+actors+"],\"trailer\":"+q(trailerUrl(p.getTitulo()))+"}");
        }
        sendJson(ex,"["+String.join(",",out)+"]");
    }

    private void showtimes(HttpExchange ex) throws IOException {
        List<String> out = new ArrayList<>(); int id=0;
        for (Funcion f: entradaServicio.getFunciones()) if (f.getPelicula().isAnime()) {
            int visible=id++;
            if (f instanceof FuncionPresencial fp) {
                int cap=fp.getSala().getCantidadButacas(), libres=cap-fp.getButacasOcupadas().size();
                out.add("{\"id\":"+visible+",\"type\":\"presencial\",\"movie\":"+q(f.getPelicula().getTitulo())+",\"date\":"+q(f.getFecha().toString())+",\"time\":"+q(f.getHorario().toString())+",\"city\":"+q(f.getCiudad())+",\"country\":"+q(f.getPais())+",\"room\":"+q(fp.getSala().getNombre())+",\"capacity\":"+cap+",\"free\":"+libres+",\"occupied\":["+fp.getButacasOcupadas().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","))+"],\"occupancy\":"+num(f.calcularOcupacion())+"}");
            } else if (f instanceof FuncionStreaming fs) {
                int free=fs.getCapacidadVirtual()-fs.getEspectadoresConectados();
                out.add("{\"id\":"+visible+",\"type\":\"streaming\",\"movie\":"+q(f.getPelicula().getTitulo())+",\"date\":"+q(f.getFecha().toString())+",\"time\":"+q(f.getHorario().toString())+",\"city\":"+q(f.getCiudad())+",\"country\":"+q(f.getPais())+",\"room\":\"Streaming\",\"capacity\":"+fs.getCapacidadVirtual()+",\"free\":"+free+",\"occupied\":[],\"occupancy\":"+num(f.calcularOcupacion())+"}");
            }
        }
        sendJson(ex,"["+String.join(",",out)+"]");
    }

    private void festival(HttpExchange ex) throws IOException {
        List<String> eds=new ArrayList<>();
        for(Edicion e:festivalServicio.getEdiciones()){
            List<String> secs=new ArrayList<>();
            for(Seccion s:e.getSecciones()){
                Pelicula w=s.determinarGanadora();
                Pelicula wp=e.determinarGanadoraPublico();
                secs.add("{\"type\":"+q(s.getTipo().name().replace('_',' '))+",\"award\":"+q(s.getPremio().getNombre())+",\"awardDescription\":"+q(s.getPremio().getDescripcion())+",\"movies\":"+s.getPeliculas().size()+",\"winner\":"+(w==null?"null":q(w.getTitulo()))+",\"publicWinner\":"+(wp==null?"null":q(wp.getTitulo()))+"}");
            }
            int peliculasCartelera = facade.cartelera(e.getAnio()).cantidadPeliculas();
            eds.add("{\"year\":"+e.getAnio()+",\"city\":"+q(e.getCiudad())+",\"start\":"+q(e.getFechaInicio().toString())+",\"end\":"+q(e.getFechaFin().toString())+",\"carteleraMovies\":"+peliculasCartelera+",\"sections\":["+String.join(",",secs)+"]}");
        }
        sendJson(ex,"{\"name\":"+q(facade.getFestival().getNombre())+",\"editions\":["+String.join(",",eds)+"],\"rooms\":["+festivalServicio.getSalas().stream().map(s->"{\"name\":"+q(s.getNombre())+",\"capacity\":"+s.getCantidadButacas()+"}").collect(java.util.stream.Collectors.joining(","))+ "]}");
    }

    private void people(HttpExchange ex) throws IOException {
        sendJson(ex,"{\"directors\":["+peliculaServicio.getDirectores().stream().map(d->"{\"name\":"+q(d.getNombre())+",\"country\":"+q(d.getNacionalidad())+"}").collect(java.util.stream.Collectors.joining(","))+"],\"actors\":["+peliculaServicio.getActores().stream().map(a->"{\"name\":"+q(a.getNombre())+",\"country\":"+q(a.getNacionalidad())+"}").collect(java.util.stream.Collectors.joining(","))+"],\"juries\":["+peliculaServicio.getJurados().stream().map(j->"{\"name\":"+q(j.getNombre())+",\"country\":"+q(j.getNacionalidad())+"}").collect(java.util.stream.Collectors.joining(","))+ "]}");
    }

    private void register(HttpExchange ex)throws IOException{try{Map<String,String>p=form(ex);Cuenta c=cuentaServicio.registrar(p.get("name"),p.get("email"),p.get("password"),p.get("country"));sendAuth(ex,c);}catch(Exception e){sendError(ex,e.getMessage());}}
    private void login(HttpExchange ex)throws IOException{try{Map<String,String>p=form(ex);sendAuth(ex,cuentaServicio.autenticar(p.get("email"),p.get("password")));}catch(Exception e){sendError(ex,e.getMessage());}}
    private void sendAuth(HttpExchange ex,Cuenta c)throws IOException{String token=sesiones.iniciar(c);sendJson(ex,"{\"ok\":true,\"token\":"+q(token)+",\"name\":"+q(c.getNombre())+",\"email\":"+q(c.getEmail())+"}");}
    private void logout(HttpExchange ex)throws IOException{sesiones.cerrar(auth(ex));sendJson(ex,"{\"ok\":true}");}

    private void reserve(HttpExchange ex)throws IOException{
        try{
            Cuenta c=sesiones.obtener(auth(ex)); if(c==null)throw new IllegalStateException("Debes iniciar sesión para reservar.");
            Map<String,String>p=form(ex); Funcion f=getShowtime(Integer.parseInt(p.get("showtimeId")));
            Entrada entrada;
            TipoTarifa tarifa = p.containsKey("tariff") && !p.get("tariff").isBlank() ? TipoTarifa.valueOf(p.get("tariff")) : TipoTarifa.GENERAL;
            if(f instanceof FuncionPresencial fp){ entrada=entradaServicio.venderEntradaPresencial(c.getNombre(),fp,Integer.parseInt(p.get("seat")),tarifa); }
            else if(f instanceof FuncionStreaming fs){ entrada=entradaServicio.venderEntradaStreaming(c.getNombre(),fs,tarifa); }
            else throw new IllegalStateException("Tipo de función no soportado.");
            int seat=entrada instanceof EntradaPresencial ep?ep.getButaca():0;
            sendJson(ex,"{\"ok\":true,\"code\":"+q(entrada.getCodigo())+",\"movie\":"+q(f.getPelicula().getTitulo())+",\"date\":"+q(f.getFecha().toString())+",\"time\":"+q(f.getHorario().toString())+",\"seat\":"+seat+",\"type\":"+q(f instanceof FuncionStreaming?"Streaming":"Presencial")+",\"room\":"+q(f instanceof FuncionPresencial fp?fp.getSala().getNombre():"Streaming")+",\"price\":"+num(entrada.getPrecio())+"}");
        }catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void history(HttpExchange ex)throws IOException{
        Cuenta c=sesiones.obtener(auth(ex)); if(c==null){sendError(ex,"Iniciá sesión para ver tu historial.");return;}
        List<String> out=new ArrayList<>();
        for(Entrada e:entradaServicio.getEntradas()) if(e.getEspectador().getEmail().equalsIgnoreCase(c.getEmail())){
            String type=e instanceof EntradaStreaming?"Streaming":"Presencial"; String detail=e instanceof EntradaPresencial ep?"Butaca "+ep.getButaca():"Acceso online";
            out.add("{\"code\":"+q(e.getCodigo())+",\"movie\":"+q(e.getFuncion().getPelicula().getTitulo())+",\"date\":"+q(e.getFuncion().getFecha().toString())+",\"time\":"+q(e.getFuncion().getHorario().toString())+",\"type\":"+q(type)+",\"detail\":"+q(detail)+"}");
        }
        sendJson(ex,"["+String.join(",",out)+"]");
    }

    private void recommendations(HttpExchange ex)throws IOException{
        List<Pelicula> recomendaciones = recomendador.recomendar(peliculaServicio.getPeliculas(),4);
        if (recomendaciones.isEmpty()) {
            // Adapter: si el motor local no tiene resultados, se adapta el proveedor externo.
            recomendaciones = new RecomendadorAdapter(new RecomendadorExterno()).recomendar(peliculaServicio.getPeliculas(),4);
        }
        List<String> out=new ArrayList<>();
        for(Pelicula p:recomendaciones) if(p.isAnime())
            out.add("{\"title\":"+q(p.getTitulo())+",\"rating\":"+num(p.calcularPromedio())+",\"image\":"+q(p.getImagenUrl())+",\"description\":"+q(p.getDescripcion())+"}");
        sendJson(ex,"["+String.join(",",out)+"]");
    }
    private void stats(HttpExchange ex)throws IOException{int pres=0,stream=0,occupied=0,capacity=0;for(Funcion f:entradaServicio.getFunciones())if(f.getPelicula().isAnime()){if(f instanceof FuncionPresencial fp){pres++;occupied+=fp.getButacasOcupadas().size();capacity+=fp.getSala().getCantidadButacas();}else if(f instanceof FuncionStreaming)stream++;}sendJson(ex,"{\"movies\":"+peliculaServicio.getPeliculas().stream().filter(Pelicula::isAnime).count()+",\"showtimes\":"+entradaServicio.getFunciones().stream().filter(f->f.getPelicula().isAnime()).count()+",\"presential\":"+pres+",\"streaming\":"+stream+",\"sold\":"+entradaServicio.getEntradas().size()+",\"occupancy\":"+(capacity==0?0:(occupied*100.0/capacity))+"}");}
    private void stream(HttpExchange ex)throws IOException{sendJson(ex,"{\"status\":\"online\",\"message\":\"Las funciones de streaming generan un código de acceso único al reservar.\"}");}

    private Funcion getShowtime(int visibleIndex){int id=0;for(Funcion f:entradaServicio.getFunciones())if(f.getPelicula().isAnime()){if(id++==visibleIndex)return f;}throw new IllegalArgumentException("Función inexistente.");}

    // ========================= GESTIÓN WEB =========================

    private void adminData(HttpExchange ex) throws IOException {
        List<String> movies = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) if (p.isAnime()) {
            movies.add("{\"title\":"+q(p.getTitulo())+",\"genre\":"+q(p.getGenero())+
                    ",\"duration\":"+p.getDuracion()+",\"director\":"+q(p.getDirector().getNombre())+
                    ",\"image\":"+q(p.getImagenUrl())+",\"description\":"+q(p.getDescripcion())+
                    ",\"rating\":"+num(p.calcularPromedio())+",\"publicRating\":"+num(p.calcularPromedioPublico())+
                    ",\"votes\":"+p.getVotosPublico().size()+",\"actors\":["+p.getActores().stream().map(a->q(a.getNombre())).collect(java.util.stream.Collectors.joining(","))+"] ,\"trailer\":"+q(trailerUrl(p.getTitulo()))+"}");
        }
        List<String> directors = peliculaServicio.getDirectores().stream()
                .map(d->"{\"name\":"+q(d.getNombre())+",\"email\":"+q(d.getEmail())+",\"country\":"+q(d.getNacionalidad())+"}").toList();
        List<String> actors = peliculaServicio.getActores().stream()
                .map(a->"{\"name\":"+q(a.getNombre())+",\"email\":"+q(a.getEmail())+",\"country\":"+q(a.getNacionalidad())+"}").toList();
        List<String> juries = peliculaServicio.getJurados().stream()
                .map(j->"{\"name\":"+q(j.getNombre())+",\"email\":"+q(j.getEmail())+",\"country\":"+q(j.getNacionalidad())+
                        ",\"evaluations\":"+j.getEvaluaciones().size()+"}").toList();
        List<String> evals = peliculaServicio.getEvaluaciones().stream()
                .filter(e->e.getPelicula().isAnime())
                .map(e->"{\"jury\":"+q(e.getJurado().getNombre())+",\"movie\":"+q(e.getPelicula().getTitulo())+",\"score\":"+num(e.getPuntaje())+"}").toList();
        List<String> votes = peliculaServicio.getVotosPublico().stream()
                .filter(v->v.getPelicula().isAnime())
                .map(v->"{\"spectator\":"+q(v.getEspectador().getNombre())+",\"movie\":"+q(v.getPelicula().getTitulo())+",\"score\":"+v.getPuntaje()+"}").toList();

        List<String> editions = new ArrayList<>();
        for (Edicion e : festivalServicio.getEdiciones()) {
            List<String> sections = new ArrayList<>();
            for (Seccion sec : e.getSecciones()) {
                Pelicula w=sec.determinarGanadora();
                Pelicula wp=e.determinarGanadoraPublico();
                sections.add("{\"type\":"+q(sec.getTipo().name())+",\"award\":"+q(sec.getPremio().getNombre())+
                        ",\"description\":"+q(sec.getPremio().getDescripcion())+
                        ",\"movies\":["+sec.getPeliculas().stream().filter(Pelicula::isAnime).map(p->q(p.getTitulo())).collect(java.util.stream.Collectors.joining(","))+"],"+
                        "\"winner\":"+(w==null?"null":q(w.getTitulo()))+
                        ",\"publicWinner\":"+(wp==null?"null":q(wp.getTitulo()))+"}");
            }
            editions.add("{\"year\":"+e.getAnio()+",\"city\":"+q(e.getCiudad())+
                    ",\"start\":"+q(e.getFechaInicio().toString())+",\"end\":"+q(e.getFechaFin().toString())+
                    ",\"sections\":["+String.join(",",sections)+"]}");
        }
        List<String> rooms = festivalServicio.getSalas().stream()
                .map(r->"{\"name\":"+q(r.getNombre())+",\"capacity\":"+r.getCantidadButacas()+"}").toList();

        List<String> spectators = entradaServicio.getEspectadores().stream()
                .map(e->"{\"name\":"+q(e.getNombre())+",\"email\":"+q(e.getEmail())+",\"country\":"+q(e.getNacionalidad())+",\"tickets\":"+e.getEntradas().size()+",\"passes\":"+e.getAbonos().size()+"}").toList();

        List<String> entries = entradaServicio.getEntradas().stream().filter(e->e.getFuncion().getPelicula().isAnime())
                .map(e->"{\"code\":"+q(e.getCodigo())+",\"movie\":"+q(e.getFuncion().getPelicula().getTitulo())+
                        ",\"spectator\":"+q(e.getEspectador().getNombre())+",\"date\":"+q(e.getFuncion().getFecha().toString())+
                        ",\"time\":"+q(e.getFuncion().getHorario().toString())+",\"type\":"+q(e instanceof EntradaStreaming?"Streaming":"Presencial")+
                        ",\"detail\":"+q(e instanceof EntradaPresencial ep?"Butaca "+ep.getButaca():"Acceso online")+
                        ",\"price\":"+num(e.getPrecio())+"}").toList();

        List<String> passes = entradaServicio.getAbonos().stream()
                .map(a->"{\"code\":"+q(a.getCodigo())+",\"type\":"+q(a.getTipo())+",\"spectator\":"+q(a.getEspectador().getNombre())+
                        ",\"tariff\":"+q(a.getTarifa().name())+",\"price\":"+num(a.getPrecio())+",\"coverage\":"+q(a.getCobertura())+
                        ",\"reservations\":"+a.getReservas().size()+"}").toList();

        sendJson(ex,"{\"movies\":["+String.join(",",movies)+"],\"directors\":["+String.join(",",directors)+
                "],\"actors\":["+String.join(",",actors)+"],\"juries\":["+String.join(",",juries)+
                "],\"evaluations\":["+String.join(",",evals)+"],\"votes\":["+String.join(",",votes)+
                "],\"editions\":["+String.join(",",editions)+"],\"rooms\":["+String.join(",",rooms)+
                "],\"spectators\":["+String.join(",",spectators)+"],\"entries\":["+String.join(",",entries)+
                "],\"passes\":["+String.join(",",passes)+"],\"tariffs\":["+java.util.Arrays.stream(TipoTarifa.values()).map(t->q(t.name())).collect(java.util.stream.Collectors.joining(","))+"]}");
    }

    private void adminDirector(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); peliculaServicio.registrarDirector(req(p,"name"),req(p,"email"),req(p,"country")); ok(ex,"Director agregado."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminActor(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); peliculaServicio.registrarActor(req(p,"name"),req(p,"email"),req(p,"country")); ok(ex,"Actor agregado."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminMovie(HttpExchange ex) throws IOException {
        try {
            Map<String,String> p=form(ex);
            String title=req(p,"title");
            peliculaServicio.registrarPelicula(title,req(p,"genre"),Integer.parseInt(req(p,"duration")),req(p,"director"));
            Pelicula movie=peliculaServicio.buscarPelicula(title);
            movie.setAnime(true);
            movie.setImagenUrl(p.getOrDefault("image",""));
            movie.setDescripcion(p.getOrDefault("description",""));
            String actors=p.getOrDefault("actors","");
            if(!actors.isBlank()) for(String actor:actors.split("\\s*;\\s*")) if(!actor.isBlank()) peliculaServicio.asociarActorAPelicula(actor,title);
            ok(ex,"Película anime agregada.");
        } catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminMovieDelete(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); peliculaServicio.eliminarPelicula(req(p,"title"),festivalServicio.getEdiciones()); ok(ex,"Película eliminada."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminMovieActor(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); Pelicula movie=peliculaServicio.buscarPelicula(req(p,"movie")); if(!movie.isAnime()) throw new IllegalArgumentException("Solo se pueden gestionar películas de anime."); peliculaServicio.asociarActorAPelicula(req(p,"actor"),movie.getTitulo()); ok(ex,"Actor asociado."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminJury(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); peliculaServicio.registrarJurado(req(p,"name"),req(p,"email"),req(p,"country")); ok(ex,"Jurado agregado."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminEvaluation(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); Pelicula movie=peliculaServicio.buscarPelicula(req(p,"movie")); if(!movie.isAnime()) throw new IllegalArgumentException("La película no pertenece al festival anime."); peliculaServicio.registrarEvaluacion(req(p,"jury"),movie.getTitulo(),Double.parseDouble(req(p,"score"))); ok(ex,"Evaluación registrada."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminEdition(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); festivalServicio.registrarEdicion(Integer.parseInt(req(p,"year")),req(p,"city"),java.time.LocalDate.parse(req(p,"start")),java.time.LocalDate.parse(req(p,"end"))); ok(ex,"Edición agregada."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminSection(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); festivalServicio.registrarSeccion(Integer.parseInt(req(p,"year")),modelo.TipoSeccion.valueOf(req(p,"type")),new Premio(req(p,"award"),p.getOrDefault("description",""))); ok(ex,"Sección y premio agregados."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminSectionMovie(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); Pelicula movie=peliculaServicio.buscarPelicula(req(p,"movie")); if(!movie.isAnime()) throw new IllegalArgumentException("Solo se pueden asociar películas anime."); festivalServicio.asociarPeliculaASeccion(Integer.parseInt(req(p,"year")),modelo.TipoSeccion.valueOf(req(p,"type")),movie); ok(ex,"Película asociada a la sección."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminSectionMovieRemove(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); Pelicula movie=peliculaServicio.buscarPelicula(req(p,"movie")); festivalServicio.buscarSeccion(Integer.parseInt(req(p,"year")),modelo.TipoSeccion.valueOf(req(p,"type"))).eliminarPelicula(movie); ok(ex,"Película quitada de la sección."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminRoom(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); festivalServicio.registrarSala(req(p,"name"),Integer.parseInt(req(p,"capacity"))); ok(ex,"Sala agregada."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminShowtime(HttpExchange ex) throws IOException {
        try {
            Map<String,String> p=form(ex);
            Pelicula movie=peliculaServicio.buscarPelicula(req(p,"movie"));
            if(!movie.isAnime()) throw new IllegalArgumentException("Solo se pueden programar películas de anime.");
            java.time.LocalDate date=java.time.LocalDate.parse(req(p,"date")); java.time.LocalTime time=java.time.LocalTime.parse(req(p,"time"));
            String city=p.getOrDefault("city","Festival Online"), country=p.getOrDefault("country","Mundo");
            if("streaming".equalsIgnoreCase(req(p,"type"))) {
                entradaServicio.programarFuncionStreaming(movie,p.getOrDefault("link",""),Integer.parseInt(req(p,"capacity")),date,time,city,country);
            } else {
                entradaServicio.programarFuncionPresencial(movie,festivalServicio.buscarSala(req(p,"room")),date,time,city,country);
            }
            ok(ex,"Función programada.");
        } catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminSpectator(HttpExchange ex) throws IOException {
        try { Map<String,String> p=form(ex); entradaServicio.registrarEspectador(req(p,"name"),req(p,"email"),req(p,"country")); ok(ex,"Espectador agregado."); }
        catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminPass(HttpExchange ex) throws IOException {
        try {
            Map<String,String> p=form(ex); String type=req(p,"type"); TipoTarifa tariff=TipoTarifa.valueOf(req(p,"tariff")); String name=req(p,"spectator");
            Abono a;
            if("TRES_DIAS".equals(type)) {
                String[] days=req(p,"days").split("\\s*,\\s*"); if(days.length!=3) throw new AbonoInvalidoException("El abono de 3 días requiere exactamente 3 fechas.");
                List<java.time.LocalDate> dates=new ArrayList<>(); for(String d:days) dates.add(java.time.LocalDate.parse(d));
                a=entradaServicio.comprarAbonoTresDias(name,tariff,dates);
            } else if("STREAMING".equals(type)) a=entradaServicio.comprarAbonoStreaming(name,tariff);
            else a=entradaServicio.comprarAbonoFull(name,tariff);
            sendJson(ex,"{\"ok\":true,\"message\":\"Abono comprado.\",\"code\":"+q(a.getCodigo())+",\"price\":"+num(a.getPrecio())+"}");
        } catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void adminPassReserve(HttpExchange ex) throws IOException {
        try {
            Map<String,String> p=form(ex); Abono a=entradaServicio.buscarAbono(req(p,"pass"));
            Funcion f=getShowtime(Integer.parseInt(req(p,"showtimeId")));
            Entrada e;
            if(f instanceof FuncionPresencial fp) e=entradaServicio.reservarPresencialConAbono(a.getCodigo(),fp,Integer.parseInt(req(p,"seat")));
            else e=entradaServicio.reservarStreamingConAbono(a.getCodigo(),(FuncionStreaming)f);
            sendJson(ex,"{\"ok\":true,\"message\":\"Reserva con abono realizada.\",\"code\":"+q(e.getCodigo())+"}");
        } catch(Exception e){sendError(ex,e.getMessage());}
    }

    private void votePublico(HttpExchange ex) throws IOException {
        try {
            Cuenta c=sesiones.obtener(auth(ex)); if(c==null) throw new IllegalStateException("Debes iniciar sesión para votar.");
            Map<String,String> p=form(ex); Espectador esp=entradaServicio.buscarEspectador(c.getNombre());
            Pelicula movie=peliculaServicio.buscarPelicula(req(p,"movie")); if(!movie.isAnime()) throw new IllegalArgumentException("Solo se votan películas de anime.");
            peliculaServicio.registrarVotoPublico(esp,movie.getTitulo(),Integer.parseInt(req(p,"score")));
            ok(ex,"Voto del público registrado.");
        } catch(Exception e){sendError(ex,e.getMessage());}
    }

    private String req(Map<String,String> p,String key){String v=p.get(key);if(v==null||v.isBlank())throw new IllegalArgumentException("Falta el campo: "+key);return v.trim();}
    private void ok(HttpExchange ex,String message)throws IOException{sendJson(ex,"{\"ok\":true,\"message\":"+q(message)+"}");}

    private void staticFiles(HttpExchange ex)throws IOException{String raw=ex.getRequestURI().getPath();String path=raw.equals("/")?"/index.html":raw;Path target=webRoot.resolve(path.substring(1)).normalize();if(!target.startsWith(webRoot)||!Files.exists(target)||Files.isDirectory(target)){ex.sendResponseHeaders(404,-1);return;}byte[]data=Files.readAllBytes(target);ex.getResponseHeaders().set("Content-Type",contentType(target));ex.getResponseHeaders().set("Cache-Control","no-cache");ex.sendResponseHeaders(200,data.length);try(OutputStream os=ex.getResponseBody()){os.write(data);}}
    private String auth(HttpExchange ex){return ex.getRequestHeaders().getFirst("Authorization");}
    private Map<String,String> form(HttpExchange ex)throws IOException{String body=new String(ex.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);Map<String,String>m=new LinkedHashMap<>();for(String part:body.split("&")){if(part.isBlank())continue;String[]kv=part.split("=",2);m.put(URLDecoder.decode(kv[0],StandardCharsets.UTF_8),kv.length>1?URLDecoder.decode(kv[1],StandardCharsets.UTF_8):"");}return m;}
    private String q(String v){if(v==null)return"\"\"";return"\""+v.replace("\\","\\\\").replace("\"","\\\"").replace("\n"," ").replace("\r"," ")+"\"";}
    private String num(double v){return String.format(Locale.US,"%.1f",v);}
    private void sendJson(HttpExchange ex,String body)throws IOException{byte[]d=body.getBytes(StandardCharsets.UTF_8);ex.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");ex.sendResponseHeaders(200,d.length);try(OutputStream os=ex.getResponseBody()){os.write(d);}}
    private void sendError(HttpExchange ex,String message)throws IOException{byte[]d=("{\"ok\":false,\"error\":"+q(message==null?"Error inesperado":message)+"}").getBytes(StandardCharsets.UTF_8);ex.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");ex.sendResponseHeaders(400,d.length);try(OutputStream os=ex.getResponseBody()){os.write(d);}}
    private String trailerUrl(String titulo) {
        return switch (titulo) {
            case "Demon Slayer: Infinity Castle" ->
                "https://www.youtube.com/embed/9kb7vK11_Rw";

            case "Puella Magi Madoka Magica: Walpurgisnacht Rising" ->
                "https://www.youtube.com/embed/iJazbGwBh-E";

            case "Chainsaw Man: Reze Arc" ->
                "https://www.youtube.com/embed/d1n552v1ng0";

            case "SHIBOYUGI: Playing Death Games" ->
                "https://www.youtube.com/embed/wq8O-IvF35I";

            case "Rascal Does Not Dream of a Bunny Girl Senpai" ->
                "https://www.youtube.com/embed/sFmnbdPwbrQ";

            case "SPY x FAMILY CODE: White" ->
                "https://www.youtube.com/embed/m5TxWbtQ7qU";

           
            case "Las Quintillizas" ->
                "https://www.youtube.com/embed/oTQXLWRDUG8";

            case "Dragon Ball Super: Broly" ->
                "https://www.youtube.com/embed/qMaoZz4C5lo";

            case "Sailor Moon Cosmos" ->
                   "https://www.youtube.com/embed/RWk4YjASwMo";

            case "Kaguya-sama: Love is War" ->
                "https://www.youtube.com/embed/rZ95aZmQu_8";
            

            default ->
                "https://www.youtube.com/results?search_query="
                    + java.net.URLEncoder.encode(
                        titulo + " trailer oficial",
                        java.nio.charset.StandardCharsets.UTF_8
                    );
        };
    }
    private String contentType(Path p){String n=p.getFileName().toString().toLowerCase();if(n.endsWith(".html"))return"text/html; charset=utf-8";if(n.endsWith(".css"))return"text/css; charset=utf-8";if(n.endsWith(".js"))return"application/javascript; charset=utf-8";if(n.endsWith(".jpg")||n.endsWith(".jpeg"))return"image/jpeg";if(n.endsWith(".png"))return"image/png";if(n.endsWith(".svg"))return"image/svg+xml";if(n.endsWith(".mp4"))return"video/mp4";if(n.endsWith(".webm"))return"video/webm";return"application/octet-stream";}
}
