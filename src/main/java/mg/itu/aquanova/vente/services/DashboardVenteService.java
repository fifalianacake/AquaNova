package mg.itu.aquanova.vente.services;

import mg.itu.aquanova.vente.dto.*;
import mg.itu.aquanova.vente.repositories.VenteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardVenteService {

    private final VenteRepository venteRepository;

    public DashboardVenteService(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    public Double getChiffreAffaires(LocalDate debut, LocalDate fin) {
        return this.venteRepository.sumChiffreAffaires(debut, fin);
    }

    public Double getVolumeEcoule(LocalDate debut, LocalDate fin) {
        return this.venteRepository.sumVolumeEcoule(debut, fin);
    }

    public Long getNombreVentes(LocalDate debut, LocalDate fin) {
        return this.venteRepository.countVentes(debut, fin);
    }

    public Double getPrixMoyenKg(LocalDate debut, LocalDate fin) {
        Double ca = this.getChiffreAffaires(debut, fin);
        Double volume = this.getVolumeEcoule(debut, fin);

        if (volume == null || volume == 0.0) return 0.0;

        return Math.round((ca / volume) * 100.0) / 100.0;
    }

    public VenteStatsDto getStats(LocalDate debut, LocalDate fin) {
        VenteStatsDto stats = new VenteStatsDto();
        stats.setChiffreAffaires(this.getChiffreAffaires(debut, fin));
        stats.setVolumeEcoule(this.getVolumeEcoule(debut, fin));
        stats.setNombreVentes(this.getNombreVentes(debut, fin));
        stats.setPrixMoyenKg(this.getPrixMoyenKg(debut, fin));
        return stats;
    }

    public List<PerformanceClientDto> getTopClients(LocalDate debut, LocalDate fin) {
        List<Object[]> rows = this.venteRepository.findTop5ClientsParCa(debut, fin);
        List<PerformanceClientDto> result = new ArrayList<>();

        for (Object[] row : rows) {
            PerformanceClientDto dto = new PerformanceClientDto();
            dto.setClientNom((String) row[0]);
            dto.setNombreVentes((Long) row[1]);
            dto.setVolumeAchete((Double) row[2]);
            dto.setChiffreAffaires((Double) row[3]);
            result.add(dto);
        }
        return result;
    }

    public List<VolumeEcouleDto> getVentesParLotOuRecolte(LocalDate debut, LocalDate fin) {
        List<Object[]> rows = this.venteRepository.findVolumeParLotEtRecolte(debut, fin);
        List<VolumeEcouleDto> result = new ArrayList<>();

        for (Object[] row : rows) {
            VolumeEcouleDto dto = new VolumeEcouleDto();
            dto.setLotNom((String) row[0]);
            dto.setRecolteReference(String.valueOf(row[1]));
            dto.setPoidsVendu((Double) row[2]);
            dto.setEffectifVendu(row[3] != null ? ((Number) row[3]).longValue() : 0L);
            dto.setMontantTotal((Double) row[4]);
            result.add(dto);
        }
        return result;
    }

    // --- Données graphiques ---

    public GraphDataDto getCaParJour(LocalDate debut, LocalDate fin) {
        List<Object[]> rows = this.venteRepository.findCaParJour(debut, fin);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add((String) row[0]);
            values.add((Double) row[1]);
        }

        GraphDataDto dto = new GraphDataDto();
        dto.setLabels(labels);
        dto.setValues(values);
        return dto;
    }

    public GraphDataDto getVolumeParLot(LocalDate debut, LocalDate fin) {
        List<Object[]> rows = this.venteRepository.findVolumeParLot(debut, fin);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add((String) row[0]);
            values.add((Double) row[1]);
        }

        GraphDataDto dto = new GraphDataDto();
        dto.setLabels(labels);
        dto.setValues(values);
        return dto;
    }

    public GraphDataDto getCaParClient(LocalDate debut, LocalDate fin) {
        List<Object[]> rows = this.venteRepository.findCaParClient(debut, fin);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add((String) row[0]);
            values.add((Double) row[1]);
        }

        GraphDataDto dto = new GraphDataDto();
        dto.setLabels(labels);
        dto.setValues(values);
        return dto;
    }

    public GraphDataDto getTop5ClientsParCa(LocalDate debut, LocalDate fin) {
        List<Object[]> rows = this.venteRepository.findTop5ClientsParCa(debut, fin);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] row : rows) {
            labels.add((String) row[0]);
            values.add((Double) row[3]); // index 3 = CA
        }

        GraphDataDto dto = new GraphDataDto();
        dto.setLabels(labels);
        dto.setValues(values);
        return dto;
    }
}