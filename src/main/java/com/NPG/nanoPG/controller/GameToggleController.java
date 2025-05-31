package com.NPG.nanoPG.controller;

import com.NPG.nanoPG.service.GameToggleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://riddlemesilly.netlify.app"
})
@RestController
@RequestMapping("/api/games")
public class GameToggleController {

    @Autowired
    private GameToggleService service;

    @GetMapping("/config")
    public Map<String, Boolean> getConfigs() {
        return service.getAll();
    }

    @GetMapping("/configs")
    public Map<String, Boolean> getConfigs1() {
        return service.getAllAndChange();
    }

    @PutMapping("/config/{key}")
    public ResponseEntity<?> update(@PathVariable String key, @RequestBody Map<String, Boolean> body) {
        boolean updated = service.updateState(key, body.get("enabled"));
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
