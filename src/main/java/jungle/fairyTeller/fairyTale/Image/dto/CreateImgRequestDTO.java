package jungle.fairyTeller.fairyTale.Image.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CreateImgRequestDTO {
    public CreateImgRequestDTO(){}

    private int batch_size;
    private int cfg_scale;
    private int denoising_strength;
    private boolean enable_hr;
    private int eta;
    private int firstphase_height;
    private int firstphase_width;
    private int height;
    private int n_iter;
    private String negative_prompt;
    private String prompt;
    private boolean restore_faces;
    private int s_churn;
    private int s_noise;
    private int s_tmax;
    private int s_tmin;
    private String sampler_index;
    private int seed;
    private int seed_resize_from_h;
    private int seed_resize_from_w;
    private int steps;
    private ArrayList styles;
    private int subseed;
    private int subseed_strength;
    private boolean tiling;
    private int width;

    public CreateImgRequestDTO(int batch_size, int cfg_scale, int denoising_strength, boolean enable_hr, int eta, int firstphase_height, int firstphase_width, int height, int n_iter, String negative_prompt, String prompt, boolean restore_faces, int s_churn, int s_noise, int s_tmax, int s_tmin, String sampler_index, int seed, int seed_resize_from_h, int seed_resize_from_w, int steps, ArrayList styles, int subseed, int subseed_strength, boolean tiling, int width) {
        this.batch_size = batch_size;
        this.cfg_scale = cfg_scale;
        this.denoising_strength = denoising_strength;
        this.enable_hr = enable_hr;
        this.eta = eta;
        this.firstphase_height = firstphase_height;
        this.firstphase_width = firstphase_width;
        this.height = height;
        this.n_iter = n_iter;
        this.negative_prompt = negative_prompt;
        this.prompt = prompt;
        this.restore_faces = restore_faces;
        this.s_churn = s_churn;
        this.s_noise = s_noise;
        this.s_tmax = s_tmax;
        this.s_tmin = s_tmin;
        this.sampler_index = sampler_index;
        this.seed = seed;
        this.seed_resize_from_h = seed_resize_from_h;
        this.seed_resize_from_w = seed_resize_from_w;
        this.steps = steps;
        this.styles = styles;
        this.subseed = subseed;
        this.subseed_strength = subseed_strength;
        this.tiling = tiling;
        this.width = width;
    }
}
