package jungle.fairyTeller.fairyTale.Image.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ImgToImgAIRequestDTO {
    public ImgToImgAIRequestDTO(){}

    private int batch_size;
    private int cfg_scale;
    private double denoising_strength;
    private int eta;
    private int height;
    private int include_init_images;
    private ArrayList<String> init_images;
    private int inpaint_full_res;
    private int inpaint_full_res_padding;
    private int inpainting_fill;
    private int inpainting_mask_invert;
    private int mask_blur;
    private int n_iter;
    private String negative_prompt;
    private Object override_settings;
    private String prompt;
    private int resize_mode;
    private int restore_faces;
    private int s_churn;
    private int s_noise;
    private int s_tmax;
    private int s_tmin;
    private String sampler_index;
    private int seed;
    private int seed_resize_from_h;
    private int seed_resize_from_w;
    private int steps;
    private ArrayList<String> styles;
    private int subseed;
    private int subseed_strength;
    private int tiling;
    private int width;

    public ImgToImgAIRequestDTO(int height, int width, ArrayList<String> init_images, String prompt, String negative_prompt) {
        this.batch_size = 1;
        this.cfg_scale = 7;
        this.denoising_strength = 0.56;
        this.eta = 0;
        this.height = height;
        this.include_init_images = 1;
        this.init_images = init_images;
        this.inpaint_full_res = 0;
        this.inpaint_full_res_padding = 0;
        this.inpainting_fill = 0;
        this.inpainting_mask_invert = 0;
        this.mask_blur = 4;
        this.n_iter = 1;
        this.negative_prompt = negative_prompt;
        this.override_settings = null;
        this.prompt = prompt;
        this.resize_mode = 0;
        this.restore_faces = 0;
        this.s_churn = 0;
        this.s_noise = 1;
        this.s_tmax = 0;
        this.s_tmin = 0;
        this.sampler_index = "Euler a";
        this.seed = -1;
        this.seed_resize_from_h = -1;
        this.seed_resize_from_w = -1;
        this.steps = 13;
        this.styles = new ArrayList<>();
        this.subseed = -1;
        this.subseed_strength = 0;
        this.tiling = 0;
        this.width = width;
    }

}
